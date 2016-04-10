(ns coldnew.config
  (:require [environ.core :as environ]
            [coldnew.config.impl :as impl]))

(defn enable-eval!
  "Set *enable-eval* to true.
  When set this, your config.edn will be evaluate after read."
  []
  (reset! impl/*enable-eval* true))

(defonce ^{:doc "A map of environment variables."}
  env
  ;; Environ redolved config in following order
  ;; 1. .lein-env file in the project directory
  ;; 2. .boot-env file in the project directory
  ;; 3. Environment variables
  ;; 4. Java system properties
  environ/env)

(defonce ^{:doc "A map of configuration and environment variables."}
  config
  (merge
   ;; 1. find config.edn in classpath
   (impl/read-resource "config.edn")
   ;; 2. If environment variable `CONFIG' is specified, also read it
   (impl/read-file (environ.core/env :config))
   ;; 3. Find info from env
   env))