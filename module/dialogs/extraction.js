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
                        .append($service, service.name),
            $fieldset = $('<fieldset/>').append($label);
        if (!service.configured) {
          $service.attr('disabled', true);
          $label.addClass('disabled');
          $label.append(' ', $('<a/>', { text: 'configure...',
                                         href: 'javascript:;',
                                         click: self.bound('showConfigurationDialog'),
                                       }));
        }
        /* Load extraction settings */
        if (service.configured && Object.keys(service.extractionSettings).length) {
          var $settings = $('<ol/>').addClass('settings').appendTo($fieldset);
          for (settingName in service.extractionSettings) {
            var settingValue = service.extractionSettings[settingName],
                $setting = $('<input/>', {
                  type: 'text',
                  name: service.name + '-' + settingName,
                  id: service.name + '-' + settingName,
                  value: settingValue
                }),
                $label = $('<label/>', {
                  'for': service.name + '-' + settingName,
                  'text': settingName
                });
            $settings.append($('<li/>').append($label).append($setting));
          };
        }
        $services.append($('<li/>').append($fieldset));
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
    var data = { column: this.column.name, services: services };

    $('.dialog-frame.ner-extraction .settings input').each(function () {
      data[$(this).attr('name')] = $(this).val();
    });

    Refine.postProcess('named-entity-recognition', 'extractions', data, {},
                      { rowsChanged: true, modelsChanged: true });
    this.hide();
  },
  
  showConfigurationDialog: function () {
    this.hide();
    new ConfigurationDialog().show();
  },
};
