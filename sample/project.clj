{:name "my-sample"
 :version "1.0"
 :namespaces '(my-sample)
 :dependencies [["tagsoup" "1.2" "org.ccil.cowan.tagsoup"]
                ;; group defaults to name
                ["rome" "0.9"]]
 :source-dependencies [["clojure-contrib" "r663" :svn
                        "http://clojure-contrib.googlecode.com/svn/trunk"]
                       ["enlive" "95b2558943f50bb9962fe7d500ede353f1b578f0"
                        :git "git://github.com/cgrand/enlive.git"]]}