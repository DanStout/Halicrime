<template>
  <gmap-map
  :center="{lat: 44.663244, lng:-63.584962}"
  :zoom="12"
  map-type-id="terrain"
  style="width: 100%; height: 100%"
>
  <gmap-marker
    v-for="crime in crimes"
    :key = crime.id
    :position = toLatLng(crime)
    :title = crime.type
  >
  </gmap-marker>
  </gmap-map>
</template>

<script>
export default {
  name: 'hello',
  data() {
    return {
      crimes: []
    }
  },
  mounted() {
    console.log(process.env.NODE_ENV === 'development')
    const baseUrl = process.env.NODE_ENV === 'development' ? 'http://localhost:7777/api/' : '/api/'
    var url = baseUrl + 'crimes'
    console.log(url)
    $.get(url)
    .done(crimes => this.crimes.push(...crimes))
    .fail(() => console.log('Failed to fetch crimes'))
  },
  methods: {
    toLatLng(crime) {
      return { lat: crime.latitude, lng: crime.longitude }
    }
  }
}
</script>

<style scoped>
h1, h2 {
  font-weight: normal;
}

ul {
  list-style-type: none;
  padding: 0;
}

li {
  display: inline-block;
  margin: 0 10px;
}

a {
  color: #42b983;
}
</style>
