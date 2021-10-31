function validatePort(rule, value, callback) {
  if (parseInt(value) < 2 || parseInt(value) > 65534) {
    callback(new Error('端口必须在2~65534之间'))
  } else {
    callback()
  }
}

export {
  validatePort
}
