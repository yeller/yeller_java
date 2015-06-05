(defproject yeller-java-api "1.1.0"
  :description "java client for yellerapp.com's exception tracking api"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :url "http://yellerapp.com/doc/java"
  :java-source-paths ["src"]
  :resource-paths ["resources"]
  :javac-options ["-target" "1.7" "-source" "1.7"]
  :dependencies [[org.apache.httpcomponents/httpclient "4.3"]
                 [org.apache.httpcomponents/fluent-hc  "4.3"]
                 [com.fasterxml.jackson.core/jackson-core "2.3.2"]
                 [com.fasterxml.jackson.core/jackson-annotations "2.3.2"]
                 [com.fasterxml.jackson.core/jackson-databind "2.3.2"]]
  :profiles {:test {:dependencies [[org.hamcrest/hamcrest-all "1.3"]
                                   [junit/junit-dep "4.11"]
                                   [org.jmock/jmock-junit4 "2.6.0"]
                                   [org.clojure/clojure "1.5.1"]]
                    :java-source-paths ["src" "test"]}})
