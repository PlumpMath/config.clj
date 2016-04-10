(defproject coldnew/config "0.1.0-SNAPSHOT"
  :description "Library for managing environment variables in Clojure using EDN configuration files."
  :author "Yen-Chin, Lee"
  :url "https://github.com/coldnew/config.clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :signing {:gpg-key "C079AE25"}
  :deploy-repositories [["releases" :clojars]]

  :jar-exclusions [#"\.cljx|\.swp|\.swo|\.DS_Store"]

  :dependencies [[org.clojure/clojure "1.8.0"]
                 [environ "1.0.2"]
                 [org.clojure/tools.logging "0.3.1"]]

  :plugins [[lein-codox "0.9.4"]]

  :codox {:source-uri "https://github.com/coldnew/config.clj/blob/master/{filepath}#L{line}"})
