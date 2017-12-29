;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns ringlet.error-test
  (:require
    [ringlet.error    :as error]
    [ringlet.response :as response]
    [clojure.test :refer [deftest is testing]]))


(deftest test-ring-escape
  (is (= {:status 400 :body "Bad request"}
        (-> (constantly :foo)
          error/ring-escape-middleware
          (apply [{:status 400 :body "Bad request"}]))) "ring-response escape")
  (is (= {:status 500 :body "Final error"}
        (-> (fn [_] {:status 500 :body "Final error"})
          error/ring-escape-middleware
          (apply [{:reason :backend-overload :product-id "1234"}])))))


(def err-handler (-> (fn [_] (response/text-500 "Server error"))
                   (error/tag-lookup-middleware error/default-tag-lookup)))


(deftest test-tagged
  (is (= {:status 400 :body "foo"
          :headers {"Content-Type" "text/plain"}}
        (err-handler {:tag :bad-input    :message "foo"})) "http 400")
  (is (= {:status 401 :body "foo"
          :headers {"Content-Type" "text/plain"
                    "WWW-Authenticate" "Basic realm=\"Access to resource\""}}
        (err-handler {:tag :unauthorized :message "foo"
                      :auth-type response/auth-basic
                      :auth-realm "Access to resource"}))  "http 401")
  (is (= {:status 403 :body "foo"
          :headers {"Content-Type" "text/plain"}}
        (err-handler {:tag :forbidden    :message "foo"})) "http 403")
  (is (= {:status 404 :body "foo"
          :headers {"Content-Type" "text/plain"}}
        (err-handler {:tag :not-found    :message "foo"})) "http 404")
  (is (= {:status 500 :body "foo"
          :headers {"Content-Type" "text/plain"}}
        (err-handler {:tag :server-error :message "foo"})) "http 500")
  (is (= {:status 503 :body "foo"
          :headers {"Content-Type" "text/plain"}}
        (err-handler {:tag :unavailable  :message "foo"})) "http 503")
  (is (= {:status 500 :body "Server error"
          :headers {"Content-Type" "text/plain"}}
        (err-handler {:backend :overload :prod-id "123"})) "fallback"))
