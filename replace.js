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


  if (result.indexOf('atoms[atm] = atoms[SPEC_OBJECT]') == -1) {
        return console.log(err);
  }
  result = result.replace(/atoms\[atm\] = atoms\[SPEC_OBJECT\]/g, 'atoms[atm] = atoms[SPEC_OBJECT].clone()');

  if (result.indexOf('atoms[atm] = atoms[atm-1]') == -1) {
          return console.log(err);
  }

  result = result.replace(/atoms\[atm\] = atoms\[atm-1\]/g, 'atoms[atm] = atoms[atm-1].clone()')

  fs.writeFile(files[0], result, 'utf8', function (err) {
     if (err) return console.log(err);
  });
});