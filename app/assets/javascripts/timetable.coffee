$ ->
  my.initAjax()

  Glob = window.Glob || {}

  apiUrl =
    send: '/send-timetable'
    getGroups: '/get-groups'
    getRooms: '/get-rooms'
    getSubject: '/get-subjects'
    getTeacher: '/get-teacher'

  vm = ko.mapping.fromJS
    selectedShift: ''
    studyShift: []
    selectedDay: ''
    weekDay: []
    selectedCouple: ''
    lCouple: []
    selectedType: ''
    typeLesson: []
    listGroups: []
    selectedGroup1: ''
    selectedGroup2: ''
    selectedGroup3: ''
    selectedGroup4: ''
    listRooms: []
    selectedRoom1: ''
    selectedRoom2: ''
    subjectList: []
    selectedSubject: ''
    listTeachers: []
    selectedTeacher1: ''
    selectedTeacher2: ''


  get = ->
    vm.studyShift([{id: 1, shift: "Morning"}, {id: 2, shift: "Afternoon"}])
    vm.weekDay([{id: 1, day: "Monday"}, {id: 2, day: "Tuesday"}, {id: 3, day: "Wednesday"}, {id: 4, day: "Thursday"},
      {id: 5, day: "Friday"}, {id: 6, day: "Saturday"}])
    vm.lCouple([{id: 1, couple: "couple 1"}, {id: 2, couple: "couple 2"}, {id: 3, couple: "couple 3"},
      {id: 4, couple: "couple 4"}])
    vm.typeLesson([{id: 1, type: "Laboratory"}, {id: 2, type: "Practice"}, {id: 3, type: "Lecture"}])

  get()

  handleError = (error) ->
    if error.status is 500 or (error.status is 400 and error.responseText)
      toastr.error(error.responseText)
    else
      toastr.error('Something went wrong! Please try again.')

  getGroupList = ->
    $.ajax
      url:apiUrl.getGroups
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.listGroups(response)

  getGroupList()

  getRooms = ->
    $.ajax
      url: apiUrl.getRooms
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.listRooms(response)

  getRooms()

  getSubjectList = ->
    $.ajax
      url: apiUrl.getSubject
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.subjectList(response)

  getSubjectList()

  getTeachers = ->
    $.ajax
      url: apiUrl.getTeacher
      type: 'GET'
    .fail handleError
    .done (response) ->
      vm.listTeachers(response)

  getTeachers()

  vm.selectedType.subscribe (type) ->
    vm.selectedType(type)


  vm.onSubmit = ->
    toastr.clear()
    if (!vm.selectedShift())
      toastr.error("Please select shift")
      return no
    else if (!vm.selectedDay())
      toastr.error("Please select day")
      return no
    else if (!vm.selectedCouple())
      toastr.error("Please select couple")
      return no
    else if (!vm.selectedSubject())
      toastr.error("Please select subject")
      return no
    else if (!vm.selectedType())
      toastr.error("Please select type")
      return no
    else if (vm.selectedType() is "Practice" && !vm.selectedTeacher1())
      toastr.error("Please select teacher")
      return no
    else if (vm.selectedType() == 'Practice' && !vm.selectedRoom1())
      toastr.error("Please select room")
      return no
    else if (vm.selectedType() is "Practice" && !vm.selectedGroup1())
      toastr.error("Please select group")
      return no
    else if (vm.selectedType() is "Laboratory" && !vm.selectedTeacher1())
      toastr.error("Please select first teacher")
      return no
    else if (vm.selectedType() is "Laboratory" && !vm.selectedTeacher2())
      toastr.error("Please select second teacher")
      return no
    else if (vm.selectedType() is "Laboratory" && vm.selectedTeacher2() == vm.selectedTeacher1())
      toastr.error("Please select another teacher")
      return no
    else if (vm.selectedType() is "Laboratory" && !vm.selectedRoom1())
      toastr.error("Please select first room")
      return no
    else if (vm.selectedType() is "Laboratory" && !vm.selectedRoom2())
      toastr.error("Please select second room")
      return no
    else if (vm.selectedType() is "Laboratory" && vm.selectedRoom2() == vm.selectedRoom1())
      toastr.error("Please select another room")
      return no
    else if (vm.selectedType() is "Laboratory" && !vm.selectedGroup1())
      toastr.error("Please select group")
      return no
    else if (vm.selectedType() is "Lecture" && !vm.selectedTeacher1())
      toastr.error("Please select teacher")
      return no
    else if (vm.selectedType() is "Lecture" && !vm.selectedRoom1())
      toastr.error("Please select room")
      return no
    else if (vm.selectedType() is "Lecture" && !vm.selectedGroup1())
      toastr.error("Please select group")
      return no

    if (vm.selectedType() is "Laboratory" && vm.selectedTeacher1() && !vm.selectedTeacher1() &&  vm.selectedRoom1() &&  !vm.selectedRoom2())
      data =
        studyShift: vm.selectedShift()
        weekDay: vm.selectedDay()
        couple: vm.selectedCouple()
        typeOfLesson: vm.selectedType()
        groups: [vm.selectedGroup1()]
        subjectId: vm.selectedSubject()
        teachers: [vm.selectedTeacher1()]
        numberRoom: [vm.selectedRoom1()]

    else if (vm.selectedType() is "Laboratory" && vm.selectedTeacher1() && vm.selectedTeacher1() &&  vm.selectedRoom1() &&  vm.selectedRoom2())
      data =
        studyShift: vm.selectedShift()
        weekDay: vm.selectedDay()
        couple: vm.selectedCouple()
        typeOfLesson: vm.selectedType()
        groups: [vm.selectedGroup1()]
        subjectId: vm.selectedSubject()
        teachers: [vm.selectedTeacher1(), vm.selectedTeacher1()]
        numberRoom: [vm.selectedRoom1(), vm.selectedRoom2()]

    else if (vm.selectedType() is "Lecture" && vm.selectedGroup1() && !vm.selectedGroup2() && !vm.selectedGroup3() && !vm.selectedGroup4())
      data =
          studyShift: vm.selectedShift()
          weekDay: vm.selectedDay()
          couple: vm.selectedCouple()
          typeOfLesson: vm.selectedType()
          groups: [vm.selectedGroup1()]
          subjectId: vm.selectedSubject()
          teachers: [vm.selectedTeacher1()]
          numberRoom: [vm.selectedRoom1()]

    else if (vm.selectedType() is "Lecture" && vm.selectedGroup1() && vm.selectedGroup2() && !vm.selectedGroup3() && !vm.selectedGroup4())
      data =
        studyShift: vm.selectedShift()
        weekDay: vm.selectedDay()
        couple: vm.selectedCouple()
        typeOfLesson: vm.selectedType()
        groups: [vm.selectedGroup1(), vm.selectedGroup2()]
        subjectId: vm.selectedSubject()
        teachers: [vm.selectedTeacher1()]
        numberRoom: [vm.selectedRoom1()]

    else if (vm.selectedType() is "Lecture" && vm.selectedGroup1() && vm.selectedGroup2() && vm.selectedGroup3() && !vm.selectedGroup4())
      data =
        studyShift: vm.selectedShift()
        weekDay: vm.selectedDay()
        couple: vm.selectedCouple()
        typeOfLesson: vm.selectedType()
        groups: [vm.selectedGroup1(), vm.selectedGroup2(), vm.selectedGroup3()]
        subjectId: vm.selectedSubject()
        teachers: [vm.selectedTeacher1()]
        numberRoom: [vm.selectedRoom1()]

    else if (vm.selectedType() is "Lecture" && vm.selectedGroup1() && vm.selectedGroup2() && vm.selectedGroup3() && vm.selectedGroup4())
      data =
        studyShift: vm.selectedShift()
        weekDay: vm.selectedDay()
        couple: vm.selectedCouple()
        typeOfLesson: vm.selectedType()
        groups: [vm.selectedGroup1(), vm.selectedGroup2(), vm.selectedGroup3(), vm.selectedGroup4()]
        subjectId: vm.selectedSubject()
        teachers: [vm.selectedTeacher1()]
        numberRoom: [vm.selectedRoom1()]

    else
      data =
        studyShift: vm.selectedShift()
        weekDay: vm.selectedDay()
        couple: vm.selectedCouple()
        typeOfLesson: vm.selectedType()
        groups: [vm.selectedGroup1()]
        subjectId: vm.selectedSubject()
        teachers: [vm.selectedTeacher1()]
        numberRoom: [vm.selectedRoom1()]

    $.ajax
      url: apiUrl.send
      type: 'POST'
      data: JSON.stringify(data)
      dataType: 'json'
      contentType: 'application/json'
    .fail handleError
    .done (response) ->
      toastr.success(response)


  ko.applyBindings {vm}