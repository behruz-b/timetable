$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    getGroupedTimetable: '/text'

  vm = ko.mapping.fromJS
    timetableList: []
    teachers: []
    timetable: []
    weekday: []
    requiredData: Glob.requiredData

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.getTable = ->
    data =
      requiredData: vm.requiredData()
    $.ajax
      url: apiUrl.getGroupedTimetable
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      for k,v of response
        if k is 'teacher'
          vm.teachers(v)
        if k is 'timetables'
          vm.timetableList(v)

  vm.getT = (teacher, weekday) ->
    tt = ko.observableArray([])
    for t in vm.timetableList()
      if t.weekDay is weekday && t.teachers is teacher
        tt.push(t)
    tt

  vm.getTable()


  ko.applyBindings {vm}