$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    getGroupedTimetable: '/groupped-timetable'

  vm = ko.mapping.fromJS
    timetableList: []
    groups: []
    timetable: []
    weekday: []

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.getTable = ->
    $.ajax
      url: apiUrl.getGroupedTimetable
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.timetableList response
      console.log(response)
      for k,v of vm.timetableList()
        vm.groups.push k
        vm.timetable v
        for key, value of v
          vm.weekday.push key


  vm.getTable()


  ko.applyBindings {vm}