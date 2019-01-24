 # Problem Overview
Hematologic malignancies, such as lymphoma, leukemia or myeloma, are the fifth most 
common type of cancer and fourth most common form of cancer-related death. In the 
U.S. alone, there are more than 1 million people who suffer from hematologic 
malignancies, with approximately 150,000 more patients diagnosed every year. 
Targeted drugs are a driving focus of drug development, but the majority of patients
eventually develop resistance, even to these new drugs. As a result, many leukemia 
patients eventually succumb to their diseases due to resistance to both conventional
chemotherapies and newer targeted agents.

In response to this issue, the Oregon Health and Science University along with 
members of the Knight Cancer Center developed the CytoScreen process to help identify
novel drugs that produce complete kill of drug-resistant cancer cells. The CytoScreen
is a miniaturized single-cell assay that is performed in multi-well chambers on a 
fluorescent microscope-imaging platform. It is an ultra-sensitive system that enables
imaging of patient blood samples with single-cell granularity. 

CytoScreening is a process requiring time-intensive scanning and acquisition of large
numbers of images. Because the drug-resistant cells that underlie cancer recurrence 
are often present at low frequencies (<0.1%) in an individual patient, the majority 
of the acquired images are not used. 


# Proposed Solution
Integrating and/or generating microscopy, image processing, and decision-making 
software that will automate and conduct real-time selective image acquisition and
filtering of the blood sample images using the microscope-computer-camera setup. 
Only blood sample images containing cancer cells will be retained. Additional 
images of the cancerous portions of the blood sample will be captured under 
different monochromatic excitation, filtering (channels), and image depths. 
These images will be used by CytoImage DX to evaluate the effectiveness of a 
particular drug treatment.

The software will automatically scan through blood samples on a microscope slide 
and capture and retain only images of interest, i.e., with cancerous cells present
in them. Measuring the fluorescent light intensity of parts of the images it will 
be determined if cancerous cells are present, based upon a user defined 
signal-background ratio threshold. This will work because the samples have been 
tagged with fluorescent dyes for certain properties. When a region of interest is
found, images of the same region will be obtained through the remaining five light
filters (channels) the Zeiss AxioImager M2 hosts at various image depths. The 
resulting system will be user-friendly, with a graphical user interface and 
significantly increase the image acquisition and filtering speed.


# Requirements
### Hardware Requirements
The system should make use of the following devices in CytoImage DX’s laboratory: 

1.	__Computer__: Dell precision T3500, with Xeon R CPU 3530 @2.8 GHz, 2 GB RAM. 
OS: Windows XP professional service pack 3;  x32 bit version.

2.	__Microscope__: Zeiss AxioImager M2 upright.

3.	__XY stage__: Marshauser Wetxiar GmbH & Co KG. Type: EK 75x50 mot. Tango 
CZ EMV

4.	__Camera__: Photometric CoolSNAP ES2.

### Software Requirements
1. Use of	__open-source automated microscopy software__ (e.g. micro-manager)
to perform rapid microscope-based scanning through blood samples.

2.	Use of __open-source imaging software__ (e.g. imagej) for the image 
acquisition and filtering process. 

3.	An __open-source integration platform__ (e.g. ICY) that will integrate the 
parts of software and perform logical decision-making for feedback to the microscope 
thus controlling the selective acquisition of microscopic images based on a 
preset user established intensity ratio in real-time. 

4.	Benchmarking the speed and accuracy of the integrated working prototype.

### Documentation Requirements
1.	Creation of an __organized online archive(s)__, e.g. a GitHub repository, that is 
easily accessible by the sponsor for viewing ALL things pertaining to the 
project listed in the subsequent enumerations.

2.	The __general status of the project__ updated at least weekly.

3.	__Dated progress reports__ listing accomplishments, issues, and future goals after
meetings. These should include specific project details, issues, and goals.

4.	Providing an updated copy of this __‘Design Specifications Document’ (DSD)__.

5.	__If requested__ by the sponsor, providing documentation that will ease the use 
or understanding of the developed software, e.g, __tutorials__, __manuals__, etc. 

# Project Progress & Status
A live Gantt chart of the project can be found at this [GitHub Wiki](https://github.com/huanlezhou/Real-time-image-capture-of-cancer-cells).

Progress reports recording accomplishments and issues at the conclusion of team meetings
can be found under the [__Progress Reports__](https://github.com/huanlezhou/Real-time-image-capture-of-cancer-cells/tree/master/Progress%20Reports) directory of this GitHub repository.
