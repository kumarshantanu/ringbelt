;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns ringbelt.response-test
  (:require
    [ringbelt.response :as response]
    [clojure.test :refer [deftest is testing]]))


(deftest test-status
  (is (= {:status 200} (response/status {})))
  (is (= {:body "hey"
          :status 209} (response/status {:body "hey"} 209))))


(deftest test-content-type
  (is (= {:headers {"Content-Type" "foo/bar"}} (response/content-type {} "foo/bar"))))


(deftest test-text-response
  (is (= {:status 200
          :body "foo"
          :headers {"Content-Type" "text/plain"}}
        (response/text-response {:body "foo"})))
  (is (= {:status 201
          :body ":foo"
          :headers {"Content-Type" "text/plain"
                    "Location" "/foo"}}
        (response/text-response {:status 201
                                 :body :foo
                                 :headers {"Location" "/foo"}}))))


(deftest test-json-response
  (is (= {:status 200
          :body "{\"foo\":10}"
          :data {:foo 10}
          :headers {"Content-Type" "application/json"}}
        (response/json-response {:data {:foo 10}})))
  (is (= {:status 201
          :body "{\"foo\":10}"
          :data {:foo 10}
          :headers {"Content-Type" "application/json"}}
        (response/json-response {:status 201
                                 :data {:foo 10}}))))


(deftest test-http-201
  (is (= {:status 201
          :headers {"Location" "/foo"}}
        (response/http-201 "/foo")))
  (is (= {:status 201
          :body "foo"
          :headers {"Location" "/foo"}}
        (response/http-201 {:body "foo"} "/foo"))))


(deftest test-http-204
  (is (= {:status 204}
        (response/http-204)))
  (is (= {:status 204
          :headers {"X-ErrorCode" "A10"}}
        (response/http-204 {:headers {"X-ErrorCode" "A10"}}))))


(deftest test-text-400
  (is (= {:status 400
          :body "foo"
          :headers {"Content-Type" "text/plain"}}
        (response/text-400 "foo")))
  (is (= {:status 400
          :body "foo"
          :headers {"Content-Type" "text/plain"
                    "X-ErrorCode"  "A10"}}
        (response/text-400 {:headers {"X-ErrorCode" "A10"}} "foo"))))


(deftest test-text-401
  (is (= {:status 401
          :body "Unauthorized"
          :headers {"Content-Type" "text/plain"
                    "WWW-Authenticate" "Basic realm=\"myrealm\""}}
        (response/text-401 "Basic" "myrealm")))
  (is (= {:status 401
          :body "User needs to authenticate"
          :headers {"Content-Type" "text/plain"
                    "WWW-Authenticate" "Basic realm=\"myrealm\""}}
        (response/text-401 {:body "User needs to authenticate"} "Basic" "myrealm"))))


(deftest test-text-403
  (is (= {:status 403
          :body "Forbidden"
          :headers {"Content-Type" "text/plain"}}
        (response/text-403 "Forbidden")))
  (is (= {:status 403
          :body "Forbidden"
          :headers {"Content-Type" "text/plain"
                    "X-Errorcode"  "A10"}}
        (response/text-403 {:headers {"X-Errorcode" "A10"}} "Forbidden"))))


(deftest test-text-404
  (is (= {:status 404
          :body "Not found"
          :headers {"Content-Type" "text/plain"}}
        (response/text-404 "Not found")))
  (is (= {:status 404
          :body "Not found"
          :headers {"Content-Type" "text/plain"
                    "X-Errorcode"  "A10"}}
        (response/text-404 {:headers {"X-Errorcode"  "A10"}} "Not found"))))


(deftest test-text-405
  (is (= {:status 405
          :body "405 HTTP Method 'POST' is not supported for this resource.\nSupported methods are: POST, PUT"
          :headers {"Allow" "POST, PUT"
                    "Content-Type" "text/plain"}}
        (response/text-405 :post "POST, PUT")))
  (is (= {:status 405
          :body "405 HTTP Method 'DELETE' is not supported for this resource.\nSupported methods are: GET, POST"
          :headers {"Allow" "GET, POST"
                    "Content-Type" "text/plain"
                    "X-Errorcode"  "A10"}}
        (response/text-405 {:headers {"X-Errorcode"  "A10"}} :delete "GET, POST"))))


(deftest test-text-500
  (is (= {:status 500
          :body "Internal error"
          :headers {"Content-Type" "text/plain"}}
        (response/text-500 "Internal error")))
  (is (= {:status 500
          :body "Internal error"
          :headers {"Content-Type" "text/plain"
                    "X-Errorcode"  "A10"}}
        (response/text-500 {:headers {"X-Errorcode"  "A10"}} "Internal error"))))


(deftest test-text-503
  (is (= {:status 503
          :body "Not available"
          :headers {"Content-Type" "text/plain"}}
        (response/text-503 "Not available")))
  (is (= {:status 503
          :body "Not available"
          :headers {"Content-Type" "text/plain"
                    "X-Errorcode"  "A10"}}
        (response/text-503 {:headers {"X-Errorcode"  "A10"}} "Not available"))))
