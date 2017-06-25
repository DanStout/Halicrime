<template>
  <div class="wrap">
      <div id="absolute-wrap">
        <div id ="daterange">
          <p>Data from {{ minDate }} to {{ maxDate }}</p>
        </div>
        <div id="buttons">
          <select v-model="selectedCrime">
              <option 
              v-for="type in crimetypes"
              :key="type"
              :value="type"
              >
              {{ titlecase(type) }}
              </option>
          </select>
        </div>
      </div>
      <gmap-map
        id="gmap"
        ref="map"
        :center="{lat: 44.663244, lng:-63.584962}"
        :zoom="12"
        :options="{fullscreenControl: false}"
        style="width: 100%; height: 100%"
      >
    </gmap-map>
  </div>

</template>

<script>
import * as util from '../util/util.js'
import * as api from '../util/api.js'
/* global google */
export default {
  data() {
    return {
      heatmap: null,
      crimegroups: null,
      selectedCrime: 'ALL',
      minDate: '?',
      maxDate: '?'
    }
  },
  mounted() {
    api.getCrimes()
    .done(crimes => {
      this.$refs.map.$mapCreated.then(map => {
        crimes.forEach(c => (c.latlng = new google.maps.LatLng(c.latitude, c.longitude)))

        // The crimes are ordered in ascending order
        this.minDate = crimes[0].date
        this.maxDate = crimes[crimes.length - 1].date

        var groups = util.groupBy(crimes, 'type')
        groups['ALL'] = crimes
        this.crimegroups = groups

        var opts = {
          data: [],
          map: map
        }
        var heatmap = new google.maps.visualization.HeatmapLayer(opts)
        heatmap.set('radius', 30)
        this.heatmap = heatmap
        this.showCrimesOfType('ALL')
      })
    })
    .fail(() => console.log('Failed to fetch crimes'))
  },
  computed: {
    crimetypes() {
      var keys = []
      for (var prop in this.crimegroups) {
        keys.push(prop)
      }
      keys.sort()
      return keys
    }
  },
  methods: {
    titlecase(str) {
      return util.titlecase(str)
    },
    showCrimesOfType(crimeType) {
      console.log('Crime type selected: ' + crimeType)
      var hmap = this.heatmap
      if (hmap) {
        hmap.setData(this.crimegroups[crimeType].map(c => c.latlng))
      }
    }
  },
  watch: {
    selectedCrime(selCrime) {
      this.showCrimesOfType(selCrime)
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
  padding:10px;
}

#daterange {
  position:absolute;
  top:0;
  right:0;
  z-index:1;
  padding:10px;
  background:rgba(255, 255, 255, 0.75);
}

@media(max-width:440px) {
  #daterange {
    position:relative;
    padding:5px;
  }

  #buttons {
    position:relative;
    padding:5px;
  }
}

#absolute-wrap {
  height:40px;
  width:100%;
  position:absolute;
}

#daterange p {
  margin:0;
  padding:0;
}

.wrap {
  width:100%;
  height:100%;
}
#gmap {
  z-index:0
}
</style>
