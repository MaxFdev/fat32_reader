# Notes

### Commands

stop: exit the program.

info: return the info no matter the dir called from.

ls: linux "ls -a", include ".." & "." & hidden folders, should be in alphabetical order.

stat {file/dir}: print the size, attributes, and next cluster for a file or dir in the current working dir. If no attribute print none, if no file/dir print error.

size {file}: prints the size of a file in bytes, if no file/is dir print error.

cd {dir}: move into the specified dir and update current working dir. Print error if arg not dir.

read {file} {offset} {bytes}: read a file from offset to offset + bytes. Print text in ascii, unless unable then print "0xNN". Print error when file is not accessible or for invalid parameters.

----------

### General

File names: files and dirs won't contain spaces

Submission: zip and name HW4.zip

Testing: run with directed output
```bash
javac *.java
java fat32_reader fat32.img < inputcommands.txt > readeroutput.tx
```

EOF: To indicate end-of-file, the entry in the FAT can be any value from 0x0FFFFFF8 to 0x0FFFFFFF (>=0x0FFFFFF8)

Short file names: short file names are 1 byte per character in ASCII / UTF-8 encoding

File name type: For this assignment, we are ignoring FAT32 long file names, so for output of ls, etc., you'll be outputting the FAT32 short file names, which are stored in all caps.

Case sensitivity: FAT32 is case-insensitive, so commands on a file or directory that exists, but in a different case than
that entered by the user, should still work.

Sectors: FAT32 file systems are composed of sectors, each of which has a fixed size in bytes. It is not the case that the file system has to completely occupy its last sector.

Int type: all values are signed in java, make sure to use a type that won't overflow

Mounting: If you want to explore fat32.img using the mount command, and mount returns to the error “ mount failed: Operation not permitted”, run the mount command using the flag “vers=2.0”

Attributes: Attribute bits are your friends, like when you’re trying to figure out if an entry is a file, subdirectory, or something else

Memory limitations: read file allocation table and the boot sector into memory and store everything else temporarily

Libraries: you may use java libraries that act on individual bytes, but not libraries that convert multiple bytes into ints, longs, etc

File name storage: For filenames in FAT32, the first 8 bytes, filled in from left to right, contain the up-to-eight characters of the name before the period. The next three bytes, filled in from left to right, contain the up-to-three characters of the name after the period. When your shell prints a filename to the screen, the period between the filename and extension, not stored in the name field, is rendered to the screen only if there
are characters in those last three bytes.
