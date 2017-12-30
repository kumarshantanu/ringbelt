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


(deftest status-test
  (is (= {:status 200} (response/status {})))
  (is (= {:body "hey"
          :status 209} (response/status {:body "hey"} 209))))


(deftest content-type-test
  (is (= {:headers {"Content-Type" "foo/bar"}} (response/content-type {} "foo/bar"))))
