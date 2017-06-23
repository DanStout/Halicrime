var inDevMode = process.env.NODE_ENV === 'development'
const baseUrl = inDevMode ? 'http://localhost:7777/api/' : '/api/'
console.log('In dev mode: ' + inDevMode + '; url: ' + baseUrl)

export function getCrimes() {
  var url = baseUrl + 'crimes'
  console.log('Fetching crimes from: ' + url)
  return $.get(url)
}
