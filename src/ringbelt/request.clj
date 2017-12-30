;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns ringbelt.request
  (:require
    [cheshire.core :as cheshire]
    [ringbelt.util :as util])
  (:import
    [com.fasterxml.jackson.core JsonParseException]))


(def valid-method-keys #{:get :head :options :put :post :delete})


(defn read-json-body
  "Given Ring request map parse body as JSON."
  [request]
  (if (when-let [type (get-in request [:headers "content-type"])]
        (not (empty? (re-find #"^application/(.+\+)?json" type))))
    (if-let [body (:body request)]
      (try
        (cheshire/parse-string (slurp body))
        (catch JsonParseException e
          (throw (IllegalArgumentException. "Malformed JSON request body" e))))
      (if (= "0" (get-in request [:headers "content-length"]))
        nil  ; 'Content-length: 0' implies null - valid JSON value
        (throw (IllegalArgumentException. "Request header 'Content-type' is valid but body not found in request"))))
    (util/expected
      "request header 'Content-type' to be 'application/json' or 'application/text+json'"
      (:content-type request))))
