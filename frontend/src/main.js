import * as Vue2GoogleMaps from 'vue2-google-maps'
import Vue from 'vue'
import App from './App'
import router from './router'

Vue.config.productionTip = false

Vue.use(Vue2GoogleMaps, {
  load: {
    key: 'AIzaSyCODQDM5ZzzOdYBBHMALrHtqjZKQ6isb54',
    libraries: 'visualization'
  }
})

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
})
