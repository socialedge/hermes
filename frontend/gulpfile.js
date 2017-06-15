var gulp = require("gulp"),
    inject = require('gulp-inject'),
    rename = require('gulp-rename'),
    mainBowerFiles = require('main-bower-files'),
    angularFilesort = require('gulp-angular-filesort'),
    webserver = require('gulp-webserver'),
    runSequence = require('run-sequence'),
    envify = require('gulp-envify'),
    args = require('yargs').argv,
    es = require('event-stream'),
    clean = require('gulp-clean');

const BUILD_DEST = './build/';
const BACKEND_DEFAULT = "http://localhost:8080";
const SERVE_PORT_DEFAULT = "8000";

gulp.task('clean', function () {
  return gulp.src(BUILD_DEST, {read: false}).pipe(clean());
});

gulp.task('build', function () {
  var backendUrl = args.backend || BACKEND_DEFAULT;

  return es.merge(
    // Index.html
    gulp.src('./app/index.html')
      // Bower dependencies
      .pipe(inject(gulp.src(mainBowerFiles()), {relative: true, name: 'bower'}))

      // Application dependencies
      .pipe(inject(gulp.src('./app/styles/**/*.css'), {relative: true, name: 'app'}))
      .pipe(inject(gulp.src('./app/scripts/**/*.js').pipe(angularFilesort()), {relative: true, name: 'app'}))

      .pipe(rename('index.html'))
      .pipe(gulp.dest(BUILD_DEST)),

    gulp.src('./app/scripts/app.js')
      .pipe(envify({
        backendBaseUrl: backendUrl
      }))
      .pipe(gulp.dest(BUILD_DEST + 'scripts/')),

    gulp.src(['./app/**/*', '!./app/index.html', '!./app/scripts/app.js'])
      .pipe(gulp.dest(BUILD_DEST))
  );
});

gulp.task('serve', function () {
  var servePort = args.port || SERVE_PORT_DEFAULT;
  return gulp
    .src(BUILD_DEST)
    .pipe(webserver({
      host: '0.0.0.0',
      port: servePort,
      livereload: false,
      directoryListing: false,
      open: false,
      fallback: 'index.html'
    }));
});

gulp.task('deploy', function () {
  runSequence('clean', 'build', 'serve');
});

gulp.task('index', ['deploy']);
