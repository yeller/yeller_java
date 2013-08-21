(defproject yeller-java-api "0.0.1-SNAPSHOT"
  :description "java client for yellerapp.com's exception tracking api"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :url "http://yellerapp.com/doc/java"
  :java-source-paths ["src"]
  :javac-options ["-target" "1.7" "-source" "1.7"]
  :dependencies [[org.apache.httpcomponents/httpclient "4.2.5"]
                 [com.fasterxml.jackson.core/jackson-core "2.2.2"]
                 [com.fasterxml.jackson.core/jackson-annotations "2.2.2"]
                 [com.fasterxml.jackson.core/jackson-databind "2.2.2"]]
  :profiles {:test {:dependencies [[junit/junit "4.11"]
                                  [org.jmock/jmock-junit4 "2.6.0"]
                                   [org.clojure/clojure "1.5.1"]
                                   [org.hamcrest/hamcrest-all "1.3"]]
                    :java-source-paths ["src" "test"]}})
