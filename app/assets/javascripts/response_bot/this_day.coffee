$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    getGroupedTimetable: '/text'
    todayTimetable: '/today-timetable'

  vm = ko.mapping.fromJS
    timetableList: []
    groups: []
    timetable: []
    weekday: []
    thisDay: ''
    studentGroup: Glob.studentGroup

  getDay = ->
    $.ajax
      url: apiUrl.todayTimetable
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.thisDay(response)
  getDay()

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.getTable = ->
    console.log('studentGroup: ',vm.studentGroup())
    data =
      group: "O'quvchi/Bugun/#{vm.studentGroup()}"
    $.ajax
      url: apiUrl.getGroupedTimetable
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      for k,v of response
        if k is 'groups'
          vm.groups(v)
        if k is 'timetables'
          vm.timetableList(v)

  vm.getT = (group, weekday) ->
    tt = ko.observableArray([])
    for t in vm.timetableList()
      if t.weekDay is weekday && t.groups is group
        tt.push(t)
    tt


  vm.getTable()


  ko.applyBindings {vm}