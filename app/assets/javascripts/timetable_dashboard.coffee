$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    getTimetable: '/get-timetable'
    getTeacherTimetable: '/get-Ttimetable'

  vm = ko.mapping.fromJS
    timetableList: []
    teacherName: ''

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
      vm.timetableList(response)

  vm.getTable()

  vm.teacherName.subscribe (name) ->
    data =
      teacherName: name
    $.ajax
      url: apiUrl.getTeacherTimetable
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (result) ->
      vm.timetableList(result)

  ko.applyBindings {vm}