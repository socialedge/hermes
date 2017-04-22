var gulp = require("gulp"),
    inject = require('gulp-inject'),
    rename = require('gulp-rename'),
    mainBowerFiles = require('main-bower-files'),
    angularFilesort = require('gulp-angular-filesort'),
    webserver = require('gulp-webserver'),
    runSequence = require('run-sequence');

gulp.task('inject', function () {
  return gulp.src('./app/app.html')
    // Bower dependencies
    .pipe(inject(gulp.src(mainBowerFiles()), {relative: true, name: 'bower'}))

    // Application dependencies
    .pipe(inject(gulp.src('./app/styles/**/*.css'), {relative: true, name: 'app'}))
    .pipe(inject(gulp.src('./app/scripts/**/*.js').pipe(angularFilesort()), {relative: true, name: 'app'}))

    .pipe(rename('index.html'))
    .pipe(gulp.dest('./app/'));
});

gulp.task('webserver', function () {
  return gulp
    .src('./app/')
    .pipe(webserver({
      livereload: true,
      directoryListing: false,
      fallback: 'index.html'
    }));
});

gulp.task('serve', function () {
  runSequence('inject', 'webserver');
});
gulp.task('index', ['serve']);
gulp.task('run', ['serve']);
