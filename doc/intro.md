# Introduction to ringlet

## Namespaces

```clojure
(require '[ringlet.error    :as error])
(require '[ringlet.request  :as request]
(require '[ringlet.response :as response]
```

## Request parsing

Ringlet supports JSON request body parsing out of the box. See example below:

```clojure
(request/read-json-body request)
```

This code reads up the request body (POST and PUT requests) as JSON, parses it as such, and returns the data
structure.


## Response generation

There are several utility functions to easily generate Ring response maps.

### HTTP status

```clojure
=> (response/status {:body "foo"})     ; default is 200
{:status 200 :body "foo"}
=> (response/status {:body "bar"} 500)
{:status 500 :body "bar"}
```


### Content type

```clojure
=> (response/content-type {:body "true"} "application/json")
{:body "true" :headers {"Content-Type" "application/json"}}
```


#### Text response

```clojure
=> (response/text-response {:status 200 :body "foo"})
{:status 200 :body "foo" :headers {"Content-Type" "text/plain"}}
```

#### JSON response

Ringlet has out of the box support for emitting JSON response.

```clojure
=> (response/json-response {:status 200 :data {:foo 10 :bar 20}})
{:status 200 :body "\"foo\": 10, \"bar\": 20" :headers {"Content-Type" "application/json"}}
```


### Cache-Control

```clojure
=> (response/cache-control {:status 200 :body "OK"} "foobar")
{:status 200 :body "OK" :headers {"Cache-Control" "foobar"}}
```

### HTTP status codes

| Status code | Function name       | Arities                                          |
|-------------|---------------------|--------------------------------------------------|
|     201     | `response/http-201` | `[url] [response url]`                           |
|     204     | `response/http-204` | `[] [response]`                                  |
|     400     | `response/text-400` | `[body] [response body]`                         |
|     401     | `response/text-401` | `[type realm] [response type realm]`             |
|     403     | `response/text-403` | `[body] [response body]`                         |
|     404     | `response/text-404` | `[body] [response body]`                         |
|     405     | `response/text-405` | `[method supported] [response method supported]` |
|     500     | `response/text-500` | `[body] [response body]`                         |
|     503     | `response/text-503` | `[body] [response body]`                         |

**Note**
- `response` is Ring response map
- `method` is string or keyword
- All other arguments are string


## Error translation

Non-web errors and failures need to be returned as Ring responses at the web layer. Ringlet introduces the
_error-handler_ abstraction, which is a function `(fn [error]) -> ring-response`, and provides utility
middleware to extend the capability of error-handler.

| Middleware                     | Arities                      |
|--------------------------------|------------------------------|
| `error/ring-escape-middleware` | `[error-handler]`            |
| `error/tag-lookup-middleware`  | `[error-handler lookup-map]` |


Example of error translation:

```clojure
;; return or throw error in non-web code as follows
{:tag :bad-input :message "Username is missing"}

;; one time definition in web layer
(def error-handler
  (-> (fn [_] {:status 500 :body "Server error"})
    (error/tag-lookup-middleware error/default-tag-lookup)))

;; translate non-web error to web error as follows
(error-handler non-web-error)  ; => {:status 400 :body "Username is missing"}
```

**Note:** For non-trivial applications you should define custom translation tags.
