;   Copyright (c) Shantanu Kumar. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file LICENSE at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.


(ns ringbelt.response
  (:require
    [clojure.string :as str]
    [cheshire.core  :as cheshire]
    [stringer.core  :as stringer]
    [ringbelt.util  :as u]))


;; ----- Generic response -----


(def default-status (fnil identity 200))


(defn status
  "Given a Ring response map, add missing or specified HTTP status."
  ([response]
   (update response :status default-status))
  ([response status-code]
   (assoc response :status status-code)))


;; ----- Response headers -----


;; binary
(def content-binary "application/octet-stream")
(def content-pdf    "application/pdf")


;; text
(def content-csv    "text/csv")
(def content-edn    "text/edn")
(def content-html   "text/html")
(def content-json   "application/json")
(def content-text   "text/plain")
(def content-xml    "text/xml")


;; blog
(def content-atom   "application/atom+xml")
(def content-rss    "application/rss+xml")


(defn content-type
  "Given a response map, associate the specified Content-Type header."
  [response content-type]
  (assoc-in response [:headers "Content-Type"] content-type))


(defn text-response
  "Given a response map, return a text response map with text/plain content-type header."
  [response]
  (let [body (:body response)]
    (if (string? body)
      (-> response
        (content-type content-text)
        status)
      (-> response
        (assoc :body (stringer/strcat body))
        (content-type content-text)
        status))))


(defn json-response
  "Given a response map with a data structure to be JSON-encoded under `:data`, return a JSON response map."
  [response]
  (if (contains? response :data)
    (let [data (:data response)]
      (-> response
        (assoc :body (cheshire/generate-string data))
        (content-type content-json)
        status))
    (u/expected ":data key in response map" response)))


(def default-cache-control (fnil identity "no-store, no-cache, must-revalidate"))


(defn cache-control
  "Given a Ring response map, add missing or specified Cache-Control header."
  ([response]
   (update-in response [:headers "Cache-Control"] default-cache-control))
  ([response header-string]
   (assoc-in response [:headers "Cache-Control"] header-string)))


;; ----- Content-type agnostic 2xx response -----


(defn http-201
  "Given a location URL return an HTTP 201 response map."
  ([url]
   {:status 201
    :headers {"Location" url}})
  ([response url]
   (-> response
     (assoc :status 201)
     (assoc-in [:headers "Location"] url))))


(defn http-204
  "Return a response map indicating HTTP 204 response without a body."
  ([]
   {:status 204})
  ([response]
   (-> response
     (assoc :status 204)
     (dissoc :body))))


;; ----- Text response -----


(defn text-400
  "Return Ring response with HTTP status 400 and specified body."
  ([body]
   (text-response {:status 400 :body body}))
  ([response body]
   (-> response
     (assoc :status 400 :body body)
     (content-type content-text))))


(def auth-basic     "Basic")
(def auth-bearer    "Bearer")
(def auth-digest    "Digest")
(def auth-hoba      "HOBA")
(def auth-mutual    "Mutual")
(def auth-negotiate "Negotiate")
(def auth-oauth     "OAuth")
(def auth-sha-1     "SCRAM-SHA-1")
(def auth-sha-256   "SCRAM-SHA-256")
(def auth-vapid     "vapid")


(defn text-401
  "Given realm return an HTTP 401 text response."
  ([type realm]
   (text-401 {} type realm))
  ([response type realm]
   (-> response
     (assoc  :status 401)
     (u/assoc-missing :body "Unauthorized")
     text-response
     (assoc-in [:headers "WWW-Authenticate"] (stringer/strfmt "%s realm=\"%s\"" type realm)))))


(defn text-403
  "Given message body, return an HTTP 403 text response."
  ([body]
   (text-response {:status 403 :body body}))
  ([response body]
   (-> response
     (assoc :status 403 :body body)
     text-response)))


(defn text-404
  "Given message body, return an HTTP 404 text response."
  ([body]
   (text-response {:status 404 :body body}))
  ([response body]
   (-> response
     (assoc :status 404 :body body)
     text-response)))


(defn text-405
  "Given a method keyword and allowed methods (comma-separated string or collection),
  return an HTTP 405 text response."
  ([method allow]
   (let [allow-str (if (string? allow) allow (u/memoized-csuv allow))]
     (text-response {:status 405
                     :headers {"Allow" allow-str}
                     :body (stringer/strcat "405 HTTP Method '"
                             (str/upper-case (u/as-str method))
                             "' is not supported for this resource.
Supported methods are: " allow-str)})))
  ([response method allow]
   (let [allow-str (if (string? allow) allow (u/memoized-csuv allow))]
    (-> response
      (assoc :status 405)
      (assoc-in [:headers "Allow"] allow-str)
      (u/assoc-missing :body (stringer/strcat "405 HTTP Method '"
                               (str/upper-case (u/as-str method))
                               "' is not supported for this resource.
Supported methods are: " allow-str))
      text-response))))


(defn text-500
  "Given message body, return an HTTP 500 text response."
  ([body]
   (text-response {:status 500 :body body}))
  ([response body]
   (-> response
     (assoc :status 500 :body body)
     text-response)))


(defn text-503
  "Given message body, return an HTTP 503 text response."
  ([body]
   (text-response {:status 503 :body body}))
  ([response body]
   (-> response
     (assoc :status 503 :body body)
     text-response)))
