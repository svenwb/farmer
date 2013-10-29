
/* Backbone extension go here */

Backbone.sync = function(method, model, options) {

    var getValue = function(object, prop) {
        if (!(object && object[prop])) return null;
        return _.isFunction(object[prop]) ? object[prop]() : object[prop];
    };

    var endpoint = getValue(model, 'endpoint');

    var requestMap = {
        'create': endpoint.insert(model.toJSON()),
        'read'  : endpoint.list(),
        'update': endpoint.update(model.toJSON()),
        'delete': endpoint.delete({ id: model.id })
    };

    var parseMap = {
        'create' : function(data) {
            if (data.result) {
                options.success(data.result);
            } else {
                options.error(data);
            }
        },
        'read' : function(data) {
            if (data.items) {
                options.success(data.items);
            } else {
                options.error(data);
            }
        },
        'update' : function(data) {
            if (data.result) {
                options.success(data.result);
            } else {
                options.error(data);
            }
        },
        'delete' : function(data) {
            if (data.error) {
                options.error(data);
            } else {
                options.success();
            }
        }
    };


    var req = requestMap[method];
    req.execute(parseMap[method]);

    return req;

};



/* Google App API go here */

var googleapiinit = function () {
	var apisToLoad;
	var callback = function () {
		if (--apisToLoad == 0) {
			signin(true, userAuthed);
		}
	}

	apiRoot = 'https://spring-bonfire-353.appspot.com/_ah/api';
	apisToLoad = 2; // must match number of calls to gapi.client.load()
	gapi.client.load('geeselistapi', 'v1', callback, apiRoot);
	gapi.client.load('oauth2', 'v2', callback);

	var signin = function (mode, callback) {
		gapi.auth.authorize({
				client_id: '1085680595177.apps.googleusercontent.com',
				scope: ['https://www.googleapis.com/auth/userinfo.email'],
				immediate: mode
			},
			callback);
	}

	var userAuthed = function () {
		var request =
			gapi.client.oauth2.userinfo.get().execute(function (resp) {
				if (!resp.code) {
					appInit();
				}
			});
	}
}


/* Models go here */
var CustomerModel = Backbone.Model.extend({
	endpoint: function() {
        return gapi.client.geeselistapi.customer;
    },

	defaults: {
		name: '',
		phone: '',
		street: '',
		city: '',
		zip: ''
	}
});

/* Views go here */


/* Routes go here */
var Router = Backbone.Router.extend({
	routes: {
		'': 'home'
	}
});


/* Instantiaion goes here */
var appInit = function() {
	var router = new Router();
	router.on('route:home', function () {
		console.log('Loaded home');
	});

	Backbone.history.start();
}