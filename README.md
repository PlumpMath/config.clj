# Config.clj
[![Circle CI](https://circleci.com/gh/coldnew/config.clj.svg?style=svg)](https://circleci.com/gh/coldnew/config.clj)
[![License](http://img.shields.io/badge/license-Eclipse-blue.svg?style=flat)](https://www.eclipse.org/legal/epl-v10.html)

Simple library for managing configuration using environment variables and EDN configuration files.

[![Clojars Project](http://clojars.org/coldnew/config/latest-version.svg)](http://clojars.org/coldnew/config)

[Latest codox API docs](https://coldnew.github.io/config.clj/).

## Usage

This library will look for `config.edn` file on the classpath or file defined with `config` environment variable. The contents of this file will be merged with the environment variables found in `System/getenv` and `System/getProperties`.

Let's create a simple testing project with following code, first add `config.edn` in `config/prod` and `config/dev` folder with setup like following:
```clojure
{:db "jdbc:postgres://localhost/prod"}
```
And build our `project.clj`
```clojure
(defproject config-test "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [coldnew/config "0.1.0-SNAPSHOT"]]
  :profiles {:prod {:resource-paths ["config/prod"]}
             :dev  {:resource-paths ["config/dev"]}}
  :main config-test.core)
```
Now you can add code in `src/config_test/core.clj` to print the variable we defined:
```clojure
(ns config-test.core
  (:require [coldnew.config :refer [env conf]]))

(defn -main []
  (println (:db env))
  (println (:db conf)))
```

The application will print the `db` variable if it defined in environment variables or `config.edn` file. The different between `env` and `conf` function are the variables resolved order.

[env](https://coldnew.github.io/config.clj/coldnew.config.html#var-env) is resolved in the following order, the variables found in later will replace those declared earlier:

1. `config.edn` on the classpath.
2. EDN file specified using the `config` environment variable.
3. `.lein-env` file in the project directory.
4. `.boot-env` file in the project directory.
5. Environment vairables.
6. Java system propertirs.

[conf](https://coldnew.github.io/config.clj/coldnew.config.html#var-conf) is like `env`, but with different priority:

1. `.lein-env` file in the project directory.
2. `.boot-env` file in the project directory.
3. Environment vairables.
4. Java system propertirs.
5. `config.edn` on the classpath.
6. EDN file specified using the `config` environment variable.

If you want to use customize config.edn file, you can setup `config` environment variable.

For example, we can create a file called `custom-config.edn` that looks as follows:
```clojure
{:db "jdbc:postgres://localhost/prod-custom"}
```
Then we start the app and pass it the `config` environment variable pointing to the location of the file:
```clojure
java -Dconfig="custom-config.edn" -jar target/config-test.jar
=> jdbc:postgres://localhost/prod-custom
```
or
```clojure
CONFIG="custon-config.edn" java -jar target/config-test.jar
=> jdbc:postgres://localhost/prod-custom
```

If you want your `config.edn` contains some function which need to be evaluated after load it, for example, your config.edn look like this:
```clojure
{:foo (System/getenv "HOME")}
```
you can use [(enable-eval!)](https://coldnew.github.io/config.clj/coldnew.config.html#var-enable-eval.21) to make this library evaluate file content:
```clojure
(ns config-test.core
  (:require [coldnew.config :as conf]))
  
(conf/enable-eval!)    ; Make coldnew.config evaluate the config after load it

;; Following will print (System/getenv "HOME") result
(println (conf/env :foo))
(println (conf/conf :foo))
```

## Disclaimer

This library is based on [environ](https://github.com/weavejester/environ) and inspired by [yogthos/config](https://github.com/yogthos/config).

## License

Copyright © 2016 Yen-Chin, Lee <<coldnew.tw@gmail.com>>

Distributed under the Eclipse Public License either version 1.0 or any later version.
