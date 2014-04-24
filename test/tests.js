// Eventually, require cdvtest plugin, for now its global

registerAutoTests('org.apache.cordova.geolocation', function() {
    var fail = function (done) {
        expect(true).toBe(false);
        done();
    },
    succeed = function (done) {
        expect(true).toBe(true);
        done();
    };

    describe('Geolocation (navigator.geolocation)', function () {

        it("geolocation.spec.1 should exist", function() {
            expect(navigator.geolocation).toBeDefined();
        });

        it("geolocation.spec.2 should contain a getCurrentPosition function", function() {
            expect(typeof navigator.geolocation.getCurrentPosition).toBeDefined();
            expect(typeof navigator.geolocation.getCurrentPosition == 'function').toBe(true);
        });

        it("geolocation.spec.3 should contain a watchPosition function", function() {
            expect(typeof navigator.geolocation.watchPosition).toBeDefined();
            expect(typeof navigator.geolocation.watchPosition == 'function').toBe(true);
        });

        it("geolocation.spec.4 should contain a clearWatch function", function() {
            expect(typeof navigator.geolocation.clearWatch).toBeDefined();
            expect(typeof navigator.geolocation.clearWatch == 'function').toBe(true);
        });

    });

    describe('getCurrentPosition method', function() {

        describe('error callback', function() {

            it("geolocation.spec.5 should be called if we set timeout to 0 and maximumAge to a very small number", function(done) {
                navigator.geolocation.getCurrentPosition(
                    fail.bind(null, done),
                    succeed.bind(null, done),
                    {
                        maximumAge: 0,
                        timeout: 0
                    });
            });

        });

        describe('success callback', function() {

            it("geolocation.spec.6 should be called with a Position object", function(done) {
                navigator.geolocation.getCurrentPosition(function (p) {
                    expect(p.coords).toBeDefined();
                    expect(p.timestamp).toBeDefined();
                    done();
                }, 
                fail.bind(null, done), 
                {
                    maximumAge:300000 // 5 minutes maximum age of cached position
                });
            });

        });

    });

    describe('watchPosition method', function() {

        describe('error callback', function() {

            var errorWatch = null;
            afterEach(function() {
                navigator.geolocation.clearWatch(errorWatch);
            });

            it("geolocation.spec.7 should be called if we set timeout to 0 and maximumAge to a very small number", function(done) {
                errorWatch = navigator.geolocation.watchPosition(
                    fail.bind(null, done),
                    succeed.bind(null, done),
                    {
                        maximumAge: 0,
                        timeout: 0
                    });
            });

        });

        describe('success callback', function() {
 
           var successWatch = null;
            afterEach(function() {
                navigator.geolocation.clearWatch(successWatch);
            });

            it("geolocation.spec.8 should be called with a Position object", function(done) {

                successWatch = navigator.geolocation.watchPosition(
                    function (p) {
                        expect(p.coords).toBeDefined();
                        expect(p.timestamp).toBeDefined();
                        done();
                    },
                    fail.bind(null, done),
                    {
                        maximumAge:(5 * 60 * 1000) // 5 minutes maximum age of cached position
                    });

            });

        });

    });

});
