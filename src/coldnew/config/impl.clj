(ns coldnew.config.impl
  (:require [environ.core :as environ]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log])
  (:import java.io.PushbackReader))

(def
  ^{:dynamic true
    :doc "If set to logical true, the configuration file will be evaluate after read."}
  *enable-eval* (atom false))

(defn eval-file [f]
  (try
    (if @*enable-eval*
      (eval f) f)
    (catch Exception e
      (log/warn (str "WARNING: failed to eval " f " " (.getLocalizedMessage e))))))

(defn read-file
  "Read and evaluate file if *enable-eval* is true."
  [f]
  (try
    (when-let [file (io/file f)]
      (when (.exists file)
        (eval-file (edn/read-string (slurp file)))))
    (catch Exception e
      (log/warn (str "WARNING: failed to parse " f " " (.getLocalizedMessage e))))))

(defn read-resource
  "Read resource file and evaluate it if *enable-eval* is true."
  [f]
  (try
    (when-let [url (io/resource f)]
      (with-open [r (-> url io/reader PushbackReader.)]
        (eval-file (edn/read r))))
    (catch Exception e
      (log/warn (str "WARNING: failed to parse " f " " (.getLocalizedMessage e))))))

(defn load-env
  "Real definition of `env` variable."
  []
  (merge
   ;; 1. Find config.edn in classpath
   (read-resource "config.edn")
   ;; 2. If environment variable `CONFIG' is specified, also read it
   (read-file (environ.core/env :config))
   ;; 3. Find info fron env
   environ/env))

(defn load-conf
  "Real definition of `conf` variable."
  []
  (merge
   ;; 1. Find info from env
   environ.core/env
   ;; 2. Find config.edn in classpath
   (read-resource "config.edn")
   ;; 3. If environment variable `CONFIG' is specified, also read it
   (read-file (environ.core/env :config))))