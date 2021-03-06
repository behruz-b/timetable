$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    getGroupedTimetable: '/groupped-timetable'

  vm = ko.mapping.fromJS
    timetableList: []
    groups: []
    timetable: []
    weekday: ["Monday", 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday']

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  vm.getTable = ->
    $('.table-responsive').css('max-height', $(window).height());
    $('#wait').show();
    $('#hide').hide();
    $.ajax
      url: apiUrl.getGroupedTimetable
      type: 'GET'
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
    $('#wait').hide();
    $('#hide').show();
    tt


  vm.getTable()


  ko.applyBindings {vm}