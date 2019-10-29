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
      console.log(response)
      for k,v of response
        if k is 'groups'
          vm.groups(v)
        if k is 'timetables'
          vm.timetableList(v)
      console.log('timetable:', vm.timetableList())
      console.log('groups:', vm.groups())

  vm.getT = (group, weekday) ->
    tt = ko.observableArray([])
    for t in vm.timetableList()
      if t.weekDay is weekday && t.groups is group
        tt.push(t)
    tt


  vm.getTable()


  ko.applyBindings {vm}