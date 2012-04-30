Mashio - a Content-injecting Rdio Proxy
==
Mashio consists of 3 parts:
  * An API proxy server which wraps the rdio API and injects custom content into their API responses
  * An AMF server to stream custom content to the rdio player
  * A web app to manage the custom content

API Proxy
==
The API proxy is build in scala with finagle and play-json to recieve, filter and manipulate API queries from the rdio appliation.

To redirect rdio's traffic to a local proxy, add `www.rdio.com` and `api.rdio.com` to localhost entry in `/etc/hosts`:
g
> 127.0.0.1 localhost api.rdio.com www.rdio.com

By default the api-proxy listens on port 4098. The rdio client defaults to making requests on port 80. To forward port 80 to 4098:
* on OSX
> sudo ipfw add 100 fwd 127.0.0.1,4098 tcp from any to me 80

Building and running the api-proxy:
> cd api-proxy

> sbt compile

> sbt run

AMF Server
==
PyAMF?

Web App
==
Play?