$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/send-group'
    getDirections: '/get-directions'
    getGroups: '/get-groups'


  vm = ko.mapping.fromJS
    name: ''
    listDirections: []
    selectedDirection: ''
    listGroups: ''

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.onSubmit = ->
    toastr.clear()
    if (!vm.name())
      toastr.error("Please enter a subject name")
      return no
    else if (vm.name().length < 6)
      toastr.error("The subject name must consist of 6 letters")
      return no
    else if (!vm.selectedDirection())
      toastr.error("Please select group's direction")
      return no

    data =
      name: vm.name()
      direction: vm.selectedDirection()
    $.ajax
      url: apiUrl.send
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      toastr.success(response)

  getDirectionList = ->
    $('#wait').show();
    $.ajax
      url:apiUrl.getDirections
      type: 'GET'
    .fail handleError
    .done (response) ->
      $('#wait').show();
      vm.listDirections(response)

  getDirectionList()

  getGroupList = ->
    $('#wait').show();
    $.ajax
      url:apiUrl.getGroups
      type: 'GET'
    .fail handleError
    .done (response) ->
      $('#wait').hide();
      vm.listGroups(response)

  getGroupList()



  ko.applyBindings {vm}