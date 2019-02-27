importClass(Packages.icy.type.collection.array.Array1DUtil)
importClass(Packages.icy.image.IcyBufferedImageUtil)
importClass(Packages.icy.sequence.SequenceUtil)
importClass(Packages.icy.file.FileUtil)
importClass(Packages.icy.image.IcyBufferedImage)
importClass(Packages.plugins.tprovoost.scripteditor.uitools.filedialogs.FileDialog)
importClass(Packages.icy.sequence.Sequence)
importClass(Packages.plugins.tprovoost.Microscopy.MicroManager.MicroManager)
importClass(Packages.plugins.tprovoost.Microscopy.MicroManager.tools.StageMover)
importClass(Packages.plugins.adufour.vars.gui.swing.ChannelSelector)
importClass(Packages.icy.file.Saver)
importClass(Packages.java.io.File)
importClass(Packages.java.util.concurrent.TimeUnit)
importClass(Packages.plugins.tprovoost.Microscopy.MicroManager.tools.MMUtils)
importClass(Packages.plugins.tprovoost.Microscopy.MicroManager.gui.MMMainFrame)

sequence0 =  new Sequence()


zoom_range = 0.5 	//Should be user input. Change later. (Microns). Indicates how far above and below z-position to acquire images.
z_increment = 0.5	//Should be user input. Change later. (Microns). Indicates z-increment for each z-slice of stacked images.
delay_interval = 500
zoom_degree = -1

channel_state = MicroManager.getCore().getProperty("ZeissReflectorTurret", "State")
TimeUnit.MILLISECONDS.sleep(delay_interval)

if (channel_state != 0){
	MicroManager.getCore().setProperty("ZeissReflectorTurret", "State", "0")
	channel_state = 0 }
println(channel_state)
TimeUnit.MILLISECONDS.sleep(delay_interval)
StageMover.moveZRelative(-z_increment)

	for (current_zoom = 0; current_zoom <= (zoom_range * 2 + 0.001); current_zoom += z_increment){
	zoom_degree += 1
	do {
		println("Channel State: "+channel_state+"    Zoom = "+current_zoom+"    Zoom degree:"+zoom_degree+"\n")
		image = MicroManager.snapImage()
		TimeUnit.MILLISECONDS.sleep(delay_interval)

		// Code for saving/manipulating images begins here
		if ((zoom_degree==0)&&(channel_state==0)){
		image0 = image}
		else if ((zoom_degree==0)&&(channel_state==1)){
		image1 = image}
		else if ((zoom_degree==0)&&(channel_state==2)){
		image2 = image}
		else if ((zoom_degree==0)&&(channel_state==3)){
		image3 = image}
		else if ((zoom_degree==0)&&(channel_state==4)){
		image4 = image}
		else if ((zoom_degree==0)&&(channel_state==5)){
		image5 = image}
		else if ((!(zoom_degree==0))&&(channel_state==0)){
			image0 = IcyBufferedImageUtil.addChannel(image0)
			image0.beginUpdate()
			dataArray = image.getDataCopyXY(0)
			doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType())
			Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image0.getDataXY(zoom_degree), image0.isSignedDataType())
			image0.endUpdate()
			}
		else if ((!(zoom_degree==0))&&(channel_state==1)){
			image1 = IcyBufferedImageUtil.addChannel(image1)
			image1.beginUpdate()
			dataArray = image.getDataCopyXY(0)
			doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType())
			Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image1.getDataXY(zoom_degree), image1.isSignedDataType())
			image1.endUpdate()
			}
		else if ((!(zoom_degree==0))&&(channel_state==2)){
			image2 = IcyBufferedImageUtil.addChannel(image2)
			image2.beginUpdate()
			dataArray = image.getDataCopyXY(0)
			doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType())
			Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image2.getDataXY(zoom_degree), image2.isSignedDataType())
			image2.endUpdate()
			}
		else if ((!(zoom_degree==0))&&(channel_state==3)){
			image3 = IcyBufferedImageUtil.addChannel(image3)
			image3.beginUpdate()
			dataArray = image.getDataCopyXY(0)
			doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType())
			Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image3.getDataXY(zoom_degree), image3.isSignedDataType())
			image3.endUpdate()
			}
		else if ((!(zoom_degree==0))&&(channel_state==4)){
			image4 = IcyBufferedImageUtil.addChannel(image4)
			image4.beginUpdate()
			dataArray = image.getDataCopyXY(0)
			doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType())
			Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image4.getDataXY(zoom_degree), image4.isSignedDataType())
			image4.endUpdate()
			}
		else if ((!(zoom_degree==0))&&(channel_state==5)){
			image5 = IcyBufferedImageUtil.addChannel(image5)
			image5.beginUpdate()
			dataArray = image.getDataCopyXY(0)
			doubleDataArray = Array1DUtil.arrayToDoubleArray(dataArray, image.isSignedDataType())
			Array1DUtil.doubleArrayToSafeArray(doubleDataArray, image5.getDataXY(zoom_degree), image5.isSignedDataType())
			image5.endUpdate()
			}

		

	if (channel_state < 5) {
		if (channel_state == "01"){
			channel_state = 1}
		channel_state = channel_state + 1 // Do NOT replace with "channel_state += 1". Causes errors, likely due to variable type casting
		MicroManager.getCore().setProperty("ZeissReflectorTurret", "State", channel_state)
		TimeUnit.MILLISECONDS.sleep(delay_interval)
		}
	else {
		channel_state = 0
		MicroManager.getCore().setProperty("ZeissReflectorTurret", "State", channel_state)
		TimeUnit.MILLISECONDS.sleep(delay_interval)
		}
		

	
	} while (channel_state != 0)
	StageMover.moveZRelative(z_increment)
	}

sequence0.setImage(0, 0, image0)
sequence0.setImage(0, 1, image1)
sequence0.setImage(0, 2, image2)
sequence0.setImage(0, 3, image3)
sequence0.setImage(0, 4, image4)
sequence0.setImage(0, 5, image5)
 
w = sequence0.getWidth()
h = sequence0.getHeight()
c = sequence0.getSizeC()
z = sequence0.getSizeZ()
t = sequence0.getSizeT()
println("Important sequence0 dimensions are:    c="+c+"    z="+z+"    t="+t)

filePath = "C:/Documents and Settings/asi/Desktop/Team4_Capstone/Test/Test10"
file = new File(filePath + "/pic_test.tif")
Saver.save(sequence0, file)
