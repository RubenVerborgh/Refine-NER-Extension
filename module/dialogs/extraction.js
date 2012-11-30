function ExtractionDialog(column) {
  this.column = column;
}

ExtractionDialog.prototype = {
  init: function (callback) {
    var self = this,
        selectedServices = {},
        dialogElement = this.dialogElement = $(DOM.loadHTML("named-entity-recognition", "dialogs/extraction.html"));
    
    /* Set labels */
    $('.column-name', dialogElement).text(this.column.name);
    
    /* Bind controls to actions */
    var controls = DOM.bind(this.dialogElement);
    controls.cancel.click(this.bound("hide"));
    controls.start.click(function () {
      self.extract(Object.keys(selectedServices).filter(function (s) { return selectedServices[s]; }));
    });
    
    /* Load service checkboxes */
    $.getJSON(NERExtension.servicesPath, function (services) {
      var $services = $(controls.services);
      services.forEach(function (service) {
        var $service = $('<input/>', {
                          type: 'checkbox',
                          change: function (event) {
                            selectedServices[service.name] = $(event.target).is(':checked');
                            updateStartStatus();
                          }}),
            $label = $('<label/>', { 'class': 'checkbox' })
                        .append($service, service.name);
        $services.append($('<li/>').append($label));
      });
      if (callback)
        callback.apply(self);
    });
    
    /* Enables or disables the start button */
    function updateStartStatus() {
      var hasServices = Object.keys(selectedServices).some(function (s) { return selectedServices[s]; });
      $(controls.start).attr('disabled', !hasServices);
    }
  },
  
  show: function () {
    this.init(function () {
      this.dialogLevel = DialogSystem.showDialog(this.dialogElement);
    });
  },
  
  hide: function () {
    DialogSystem.dismissUntil(this.dialogLevel - 1);
  },
  
  extract: function (services) {
    alert("Extract with " + JSON.stringify(services));
    this.hide();
  }
};
