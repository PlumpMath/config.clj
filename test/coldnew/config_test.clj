(ns coldnew.config-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [coldnew.config :as conf]))

(defn refresh-ns []
  (ns-unalias *ns* 'conf)
  (remove-ns 'coldnew.config)
  (dosync (alter @#'clojure.core/*loaded-libs* disj 'coldnew.config))
  (require '[coldnew.config :as conf]))

(defn refresh-env []
  (eval `(do (refresh-ns) conf/env)))

(defn refresh-conf []
  (eval `(do (refresh-ns) conf/conf)))

(deftest test-env
  (testing "env variables"
    (let [env (refresh-env)]
      (is (= (:user env) (System/getenv "USER")))
      (is (= (:java-arch env) (System/getenv "JAVA_ARCH")))))
  (testing "system properties"
    (let [env (refresh-env)]
      (is (= (:user-name env) (System/getProperty "user.name")))
      (is (= (:user-country env) (System/getProperty "user.country"))))))

;; Testing config with config.edn file
(deftest test-conf
  (testing "conf file"
    (let [conf (refresh-conf)]
      (is (= (:foo conf) "bar"))))
  (testing "conf file with eval"
    ;; make config can be evaluated
    (conf/enable-eval!)
    (let [conf (refresh-conf)]
      (is (= (:home2 conf) (System/getenv "HOME"))))))