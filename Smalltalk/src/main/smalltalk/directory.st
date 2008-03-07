Object subclass: #Directory
   instanceVariableNames: 'name number'
   classVariableNames: ''
   poolDictionaries: ''
   category: ''!
Object subclass: #DirectoryEntry
   instanceVariableNames: 'fileno reclen type namlen name'
   classVariableNames: ''
   poolDictionaries: ''
   category: ''!

!Directory class methodsFor: ''!

   open: dirName
      ^ self new name: dirName number: <124 1 dirName>
   !
!


!Directory methodsFor: ''!

   close
      <124 2 number>.
      name := nil.
      number := nil.
   !
   do: aBlock
      | entry |
      [ entry := self read. entry notNil ] whileTrue: [
         aBlock value: entry.
         ].
   !
   name
      ^ name 
   !
   name: dirName number: dirNum
      name := dirName. 
      number := dirNum
   !
   number
      ^ number
   !
   read
      ^ <124 3 number>.
   !
   rewind
      ^ <124 6 number>.
   !
   seek: location
      ^ <124 5 number location>.
   !
   tell
      ^ <124 4 number>.
   !
!


!DirectoryEntry methodsFor: ''!
   fileno
      ^ fileno
   !
   reclen
      ^ reclen
   !
   type
      ^ type
   !
   namlen
      ^ namlen
   !
   name
      ^ name
   !
!



