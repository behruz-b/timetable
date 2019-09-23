$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    getTimetable: '/get-timetable'

  vm = ko.mapping.fromJS
    timetableList: []

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')


  vm.getTable = ->
    $.ajax
      url: apiUrl.getTimetable
      type: 'GET'
    .fail handleError
    .done (response) ->
      console.log(response)
      vm.timetableList(response)

  vm.getTable()

  ko.applyBindings {vm}