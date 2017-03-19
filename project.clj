(defproject livewire "0.1.0-SNAPSHOT"
  :description "A websocket Clojure app, broadcasting Postgres notifications to authenticated clients."
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.8.0"],
                 [org.clojure/tools.logging "0.3.1"],
                 [com.impossibl.pgjdbc-ng/pgjdbc-ng "0.7.1"],
                 [compojure "1.5.1"],
                 [cheshire "5.6.3"],
                 [http-kit "2.2.0"],
                 [environ "1.1.0"]]
  :main ^:skip-aot livewire.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
