Magnitude subclass: #Date  
   instanceVariableNames: 'date time'
   classVariableNames: 'SecondsInDay MonthNames FirstDayOfMonth DaysInMonth Week
DayNames'
   poolDictionaries: ''
   category: 'DateTime'!


!Date class methodsFor: '' !

	initialize
		"Initialize class variables with common date stuff"
		DaysInMonth := #(31 28 31 30 31 30 31 31 30 31 30 31 ).
		FirstDayOfMonth := #(1 32 60 91 121 152 182 213 244 274 305 335 ).
		MonthNames := #(January February March April May June July August September October November December ).
		SecondsInDay := 24 * 60 * 60.
		WeekDayNames := #(Monday Tuesday Wednesday Thursday Friday Saturday Sunday )
	!
	DaysInMonth
		^ DaysInMonth
	!
	FirstDayOfMonth
		^ FirstDayOfMonth
	!
	MonthNames
		^ MonthNames
	!
	SecondsInDay
		^ SecondsInDay
	!
	WeekDayNames
		^ WeekDayNames
	!
	dayOfWeek: dayName
		(1 to: 7) do: [ :x |
			((WeekDayNames at: x) == (dayName asSymbol)) ifTrue: [ ^x ]
			].
		^nil
	!
	leapYear: aYear
		" return true if aYear is a leap year "
		(aYear \\ 4 == 0) ifTrue:[
			(aYear \\ 100 ~= 0) ifTrue:[^ true].
			(aYear \\ 400 == 0) ifTrue:[^ true]
			].
		^ false
	!
	localtime: aDateTime
		" return localtime() bits for a date/time"
		^ <99 (aDateTime date) (aDateTime time)>
	!
	now
		" assign the current date and time"
		| aDate datetime |
		datetime := <100>.
		^ aDate := Date new; date: (datetime at: 1); time: (datetime at: 2); yourself.
	!
	today
		^ self now
	!
	format: aDateTime with: aFormat
		"provides an interface to the strftime function"
		^ <98 (aDateTime date) (aDateTime time) aFormat>
	!
!


!Date methodsFor: '' !

	= aDate
		(self class = aDate class) ifFalse: [ ^false ].
		((self date = aDate date) and: [self time = aDate time])
			ifTrue: [ ^true  ]
			ifFalse: [ ^false ].
	!
	addDays: days
		| nDate |
		nDate := self class new.
		nDate date: (self date) + days.
		nDate time: (self time).
		^ nDate
	!
	date
		^ date
	!
	date: aDate
		^ date := aDate
	!
	day
		^ (Date localtime: self) at: 4
	!
	dayOfWeek
		^ (Date localtime: self) at: 7
	!
	dayOfWeekName
		^ Date WeekDayNames at: (self dayOfWeek)
	!
	dayOfYear
		^ (Date localtime: self) at: 8
	!
	format: aFormat
		^ self class format: self with: aFormat
	!
	hours
		^ (Date localtime: self) at: 3
	!
	isLeapYear
		^ Date leapYear: (self year)
	!
	minutes
		^ (Date localtime: self) at: 2
	!
	month
		^ (Date localtime: self) at: 5
	!
	monthName
		^ Date MonthNames at: (self month)
	!
	mmddyy
		^ self class format: self with: '%m-%d-%y'
	!
	next: dayName 
		"Return the next date whose weekday name is dayName"
		| diff |
		diff := (self class dayOfWeek: dayName) - self dayOfWeek.
		(diff == 0 )
			ifTrue: [ diff := 7]
			ifFalse: [
				(diff < 0)
					ifTrue: [ diff := (7 + diff) ].
				].
	 ^ self addDays: diff
	!
	now
		"assign the current date and time to a Date instance"
		| datetime |
		datetime := <100>.
		self date: (datetime at: 1).
		self time: (datetime at: 2).
		^ self
	!
	previous: dayName 
	 "Return the previous date whose weekday name is dayName"
	 ^ self subtractDays:(self dayOfWeek - (self class dayOfWeek: dayName) - 1 \\ 7)
	!
	seconds
		^ (Date localtime: self) at: 1
	!
	subtractDays: days
		| nDate |
		nDate := self class new.
		nDate date: (self date) - days.
		^ nDate
	!
	time
		^ time
	!
	time: aTime
		^ time := aTime
	!
	timezone
		^ (Date localtime: self) at: 11
	!
	year
		^ (Date localtime: self) at: 6
	!
!

Date initialize.
!
