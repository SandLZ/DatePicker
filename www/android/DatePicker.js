/**
 * Created by liuzhu on 16/10/17.
 */
/**
 * Constructor
 */
function DatePicker() {
	//this._callback;
}

/**
 * Android themes
 */
DatePicker.prototype.ANDROID_THEMES = {
	THEME_TRADITIONAL          : 1, // default
	THEME_HOLO_DARK            : 2,
	THEME_HOLO_LIGHT           : 3,
	THEME_DEVICE_DEFAULT_DARK  : 4,
	THEME_DEVICE_DEFAULT_LIGHT : 5
};

/**
 * show - true to show the ad, false to hide the ad
 */
DatePicker.prototype.show = function(options, cb, errCb) {

	if (options.date && options.date instanceof Date) {
		options.date = (options.date.getMonth() + 1) + "/" +
			(options.date.getDate()) + "/" +
			(options.date.getFullYear()) + "/" +
			(options.date.getHours()) + "/" +
			(options.date.getMinutes()) + "/" +
			(options.date.getSeconds());
	}
    if (options.maxDate && !(options.maxDate instanceof Date)) {
        options.maxDate = new Date(options.maxDate)
    }
    if (options.maxDate && options.maxDate instanceof Date) {
        options.maxDate = (options.maxDate.getMonth() + 1) + "/" +
            (options.maxDate.getDate()) + "/" +
            (options.maxDate.getFullYear()) + "/" +
            (options.maxDate.getHours()) + "/" +
            (options.maxDate.getMinutes()) + "/" +
            (options.maxDate.getSeconds());
    }

	var defaults = {
		type : 2,
		weekIndex:1,
		weekNameArr:[],
		weeKMaxIndex: -1,
		showSecond:true,
		date : '',
		minDate: 0,
		maxDate: '0',
		titleText: '',
		cancelText: '',
		okText: '',
		todayText: '',
		nowText: '',
		is24Hour: false,
		androidTheme : window.datePicker.ANDROID_THEMES.THEME_TRADITIONAL, // Default theme
	};

	for (var key in defaults) {
		if (typeof options[key] !== "undefined") {
			defaults[key] = options[key];
		}
	}

	//this._callback = cb;

	var callback = function(message) {
		if(message != 'error'){
			cb(message);
		} else {
			// TODO error popup?
		}
	};

	var errCallback = function(message) {
		if (typeof errCb === 'function') {
			errCb(message);
		}
	};

	cordova.exec(callback,
		errCallback,
		"DatePicker",
		"show",
		[defaults]
	);
};

var datePicker = new DatePicker();
module.exports = datePicker;

// Make plugin work under window.plugins
if (!window.plugins) {
	window.plugins = {};
}
if (!window.plugins.datePicker) {
	window.plugins.datePicker = datePicker;
}
