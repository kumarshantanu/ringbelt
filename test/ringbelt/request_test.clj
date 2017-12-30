;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns ringbelt.request-test
  (:require
    [ringbelt.request  :as request]
    [ring.mock.request :as mock]
    [clojure.test :refer [deftest is testing]]))


(deftest test-read-json-body
  (is (= {"foo" 10}
        (-> (mock/request :post "/api/endpoint")
          (mock/json-body {:foo 10})
          request/read-json-body)))
  (is (= nil
        (-> (mock/request :post "/api/endpoint")
          (mock/json-body nil)
          request/read-json-body)))
  (is (thrown? IllegalArgumentException
        (-> (mock/request :post "/api/endpoint")
          (mock/content-type "application/json")
          (mock/body "foo")
          request/read-json-body))))
