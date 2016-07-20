var gulp = require("gulp");
var plug = require("gulp-load-plugins")();
var inject = require('gulp-inject');

gulp.task('index', function () {
    var scripts = gulp.src([
        './app/vendor/angular/*.min.js',
        './app/vendor/angular-*/*.min.js',
        './app/vendor/jquery/**/*.min.js',
        './app/vendor/bootstrap/**/*.min.js',
        './app/scripts/**/*.js'
    ], {read: false});

    var styles = gulp.src([
        './app/vendor/bootstrap/**/*.min.css',
        '!./app/vendor/bootstrap/**/*theme.min.css', // no bootstrap theme
        './app/styles/**/*.css'
    ], {read: false});

    return gulp.src('./app/index.html')
        .pipe(inject(scripts, {relative: true}))
        .pipe(inject(styles, {relative: true}))
        .pipe(gulp.dest('./app/'));

});

gulp.task('serve', function () {
    return gulp
        .src('./app/')
        .pipe(plug.webserver({
            livereload: true,
            directoryListing: false,
            fallback: 'index.html'
        }));
});

gulp.task("default", ["serve"]);