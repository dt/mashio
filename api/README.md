To redirect rdio's traffic to a local proxy, add `www.rdio.com` and `api.rdio.com` to localhost entry in `/etc/hosts`:
g
> 127.0.0.1 localhost api.rdio.com www.rdio.com m.rdio.com

By default the api-proxy listens on port 4098. The rdio client defaults to making requests on port 80. To forward port 80 to 4098 on OSX:

> sudo ipfw add 100 fwd 127.0.0.1,4098 tcp from any to me 80

Building and running the api-proxy:
> cd api-proxy

> sbt compile

> sbt run
