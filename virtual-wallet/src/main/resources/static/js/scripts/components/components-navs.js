(function (window, document, $) {
  'use strict';

  // add height to navigation in left tabs according to content area height
  var heightLeft = $('.nav-left + .tab-content').height();

  $('ul.nav-left').height(heightLeft);

  // add height to navigation in right tabs according to content area height
  var heightRight = $('.nav-right + .tab-content').height();

  $('ul.nav-right').height(heightRight);
})(window, document, jQuery);
