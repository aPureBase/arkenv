FROM alpine/bundle:2.7.2

COPY . .

RUN bundle install --path vendor/bundle \
  && bundle exec jekyll build -d /public

FROM nginx
COPY --from=0 /public /usr/share/nginx/html

