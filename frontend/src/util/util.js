export function titlecase(str) {
  var s1 = ''
  var firstOrAfterSpace = true
  for (var i = 0; i < str.length; i++) {
    var c = str.charAt(i)

    if (c === ' ') {
      firstOrAfterSpace = true
      s1 += c
      continue
    }

    if (firstOrAfterSpace) {
      c = c.toUpperCase()
      firstOrAfterSpace = false
    } else {
      c = c.toLowerCase()
    }

    s1 += c
  }
  return s1
}

export function groupBy(list, property) {
  return list.reduce((map, value) => {
    var key = value[property];
    (map[key] = map[key] || []).push(value)
    return map
  }, {})
}

export function merge(src, dest) {
  for (var prop in src) {
    dest[prop] = src[prop]
  }
  return dest
}
