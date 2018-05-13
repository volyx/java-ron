const files = process.argv.slice();
files.shift();
files.shift();

console.log('files', files)

var fs = require('fs')
fs.readFile(files[0], 'utf8', function (err,data) {
  if (err) {
    return console.log(err);
  }
  var result = data.replace(/frame\.Body\)\[(\w+)\]/g, 'frame.Body).get(p)');

  fs.writeFile(files[0], result, 'utf8', function (err) {
     if (err) return console.log(err);
  });
});