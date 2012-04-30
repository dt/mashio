Mashio consists of 3 parts:
  * An API proxy server which wraps the rdio API and injects custom content into their API responses
  * An AMF server to stream custom content to the rdio player
  * A web app to manage the custom content

API Proxy
==
The API proxy is build in scala with finagle and play-json to recieve, filter and manipulate API queries from the rdio appliation.

AMF Server
==
PyAMF?

Web App
==
Play?