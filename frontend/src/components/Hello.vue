<template>
  <div class="wrap">
      <div id="buttons">
        <select v-model="selectedCrime">
            <option 
            v-for="(crimes, type) in crimegroups"
            v-bind:key="type"
            v-bind:value="type"
            >
            {{ type }}
            </option>
        </select>
      </div>
      <gmap-map
        id="gmap"
        ref="map"
        :center="{lat: 44.663244, lng:-63.584962}"
        :zoom="12"
        style="width: 100%; height: 100%"
      >
    </gmap-map>
  </div>

</template>

<script>
/* global google */
export default {
  name: 'hello',
  data() {
    return {
      heatmap: null,
      crimegroups: null,
      selectedCrime: 'ALL'
    }
  },
  mounted() {
    console.log(process.env.NODE_ENV === 'development')
    const baseUrl = process.env.NODE_ENV === 'development' ? 'http://localhost:7777/api/' : '/api/'
    var url = baseUrl + 'crimes'
    console.log(url)
    $.get(url)
    .done(crimes => {
      this.$refs.map.$mapCreated.then(map => {
        crimes.forEach(c => (c.latlng = new google.maps.LatLng(c.latitude, c.longitude)))

        var groups = this.groupBy(crimes, 'type')
        groups['ALL'] = crimes
        this.crimegroups = groups

        var opts = {
          data: [],
          map: map
        }
        var heatmap = new google.maps.visualization.HeatmapLayer(opts)
        heatmap.set('radius', 30)
        this.heatmap = heatmap
        this.setCrimeToType('ALL')
      })
    })
    .fail(() => console.log('Failed to fetch crimes'))
  },
  methods: {
    groupBy(list, property) {
      return list.reduce((map, value) => {
        var key = value[property];
        (map[key] = map[key] || []).push(value)
        return map
      }, {})
    },
    clicked() {
      this.crimes.push({})
    },
    toLatLng(crime) {
      return { lat: crime.latitude, lng: crime.longitude }
    },
    setCrimeToType(crimeType) {
      console.log('Crime type selected: ' + crimeType)
      var hmap = this.heatmap
      if (hmap) {
        hmap.setData(this.crimegroups[crimeType].map(c => c.latlng))
      }
    }
  },
  watch: {
    selectedCrime(selCrime) {
      this.setCrimeToType(selCrime)
    }
  }
}
</script>

<style>
.gmnoprint {
  display:none;
}
#buttons {
  position:absolute;
  top:0;
  left:0;
  z-index:1;
}
.wrap {
  width:100%;
  height:100%;
}
#gmap {
  z-index:0
}
</style>
