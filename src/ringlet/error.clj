;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns ringlet.error
  "An error handler is a function `(fn [error]) -> ring-response-map` - the argument error is typically a map."
  (:require
    [ringlet.response :as response]))


(defn ring-escape-middleware
  "Given an error handler (fn [error]) wrap it such that the error is returned as-is if it is a Ring response."
  [error-handler]
  (fn [error]
    (if (and (map? error)
          (let [status (:status error)]
            (and (integer? status)
              (< 99 status 600))))
      error
      (error-handler error))))


(defn tag-lookup-middleware
  "Given error handler (fn [error]) wrap it such that it first looks up :tag value in error and calls respective
  error-handler."
  [error-handler lookup]
  (fn [error]
    (if-let [tagged-handler (and (map? error)
                              (get lookup (:tag error)))]
      (tagged-handler error)
      (error-handler error))))


(def default-tag-lookup
  "A lookup map of tag to error-translator."
  {:bad-input    (fn [error] (response/text-400 (or (:message error) "Bad input")))
   :unauthorized (fn [error] (let [{:keys [message auth-type auth-realm]} error]
                               (if message
                                 (response/text-401 {:body message} auth-type auth-realm)
                                 (response/text-401 auth-type auth-realm))))
   :forbidden    (fn [error] (response/text-403 (or (:message error) "Forbidden")))
   :not-found    (fn [error] (response/text-404 (or (:message error) "Not found")))
   :server-error (fn [error] (response/text-500 (or (:message error) "Server error")))
   :unavailable  (fn [error] (response/text-503 (or (:message error) "Temporarily unavailable")))})
