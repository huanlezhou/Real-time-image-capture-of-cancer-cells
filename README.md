# Real-time-image-capture-of-cancer-cells
An embedded system for microscope-based, real-time image capture of cancer cells. 



Capstone Project Proposal:
An embedded system for microscope-based, real-time image capture of cancer cells

Overview: 
Hematologic malignancies are the fifth most common type of cancer and fourth most common form of cancer-related death. More than 1 million people in the U.S. suffer from a hematologic malignancy; every year, another 150,000 patients are diagnosed with lymphoma, leukemia or myeloma. Targeted drugs are a driving focus of drug development, but the majority of patients eventually develop resistance even to these new drugs. As a result, many leukemia patients eventually succumb to their diseases due to resistance to both conventional chemotherapies and newer targeted agents. There is an urgent need to identify novel drugs that produce complete kill of drug-resistant cancer cells. In collaboration with members of the Knight Cancer Center, Oregon Health and Science University, we have developed the CytoScreen, a miniaturized single-cell assay that is performed in multi-well chambers on a fluorescent microscope-imaging platform. This is an ultrasensitive system that enables imaging of patient blood samples with single-cell granularity. This system is currently being used to screen for effective drug combinations in individual leukemia patients. 

Problem: Drug screening is a process requiring time-intensive scanning and acquisition of large numbers of images. Because the drug-resistant cells that underlie cancer recurrence are often present at low frequency (<0.1%) in an individual patient, the majority of scanned images are not used. Software that interfaces with microscope hardware to selectively assess and selectively acquire images containing drug-resistant cells would provide faster outcome reporting, potentially impacting treatment decisions and benefiting lives.

Project Deliverables: A software/hardware prototype system that provides automated, selective acquisition of microscope images based on pre-set user-established intensity thresholds, in real-time.

1 Implementation of open source automated microscopy software (https://micro-manager.org/) to perform rapid microscope-based scanning of patient samples.
 
2 Implementation of open source imaging software (https://imagej.nih.gov/ij/) to rapidly evaluate and detect microscope-scan of clinical interest in real-time.

3 Implementation of software that uses decision-making, feedback-based microscope scanned information to control acquisition of microscope images.

4 Benchmarking the speed and accuracy of the integrated working prototype.
