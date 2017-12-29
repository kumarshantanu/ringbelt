(defproject ringlet "0.1.0-SNAPSHOT"
  :description "Utility tool belt for Ring web applications"
  :url "https://github.com/kumarshantanu/ringlet"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[cheshire  "5.8.0"]
                 [stringer  "0.3.0"]]
  :global-vars {*warn-on-reflection* true
                *assert* true
                *unchecked-math* :warn-on-boxed}
  :min-lein-version "2.7.1"
  :pedantic? :abort
  :profiles {:provided {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :c17 {:dependencies [[org.clojure/clojure "1.7.0"]]}
             :c18 {:dependencies [[org.clojure/clojure "1.8.0"]]}
             :c19 {:dependencies [[org.clojure/clojure "1.9.0"]]}
             :dln {:jvm-opts ["-Dclojure.compiler.direct-linking=true"]}})
