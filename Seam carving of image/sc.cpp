
#include "sc.h"

using namespace cv;
using namespace std;


class Pixel {
public:
	int gray;
	int energy;
	int path;
};

Pixel image[5000][5000];//save each pixel's information


//Turn a image into a grayscale image
void grayImage(cv::Mat &input, cv::Mat &output)
{
	for (int i = 0; i < input.rows; ++i)
	{
		for (int j = 0; j < input.cols; ++j)
		{
			output.at<uchar>(i, j) = cv::saturate_cast<uchar>(0.114*input.at<cv::Vec3b>(i, j)[0] + 0.587*input.at<cv::Vec3b>(i, j)[1] + 0.2989*input.at<cv::Vec3b>(i, j)[2]);
		}
	}

	for (int i = 0; i < input.cols; ++i) {
		for (int j = 0; j < input.rows; ++j) {
			image[i][j].gray = output.at<uchar>(j, i);
			image[i][j].energy = output.at<uchar>(j, i);
			image[i][j].path= 0;
		}
	}
}



bool seam_carving(Mat& in_image, int new_width, int new_height, Mat& out_image){

    // some sanity checks
    // Check 1 -> new_width <= in_image.cols
    if(new_width>in_image.cols){
        cout<<"Invalid request!!! new_width has to be smaller than the current size!"<<endl;
        return false;
    }
    if(new_height>in_image.rows){
        cout<<"Invalid request!!! ne_height has to be smaller than the current size!"<<endl;
        return false;
    }
    
    if(new_width<=0){
        cout<<"Invalid request!!! new_width has to be positive!"<<endl;
        return false;

    }
    
    if(new_height<=0){
        cout<<"Invalid request!!! new_height has to be positive!"<<endl;
        return false;
        
    }

    
    return seam_carving_trivial(in_image, new_width, new_height, out_image);
}


// seam carves by removing trivial seams
bool seam_carving_trivial(Mat& in_image, int new_width, int new_height, Mat& out_image){

    Mat iimage = in_image.clone();
    Mat oimage = in_image.clone();
    while(iimage.rows!=new_height || iimage.cols!=new_width){
        // horizontal seam if needed
        if(iimage.rows>new_height){
            reduce_horizontal_seam_trivial(iimage, oimage);
            iimage = oimage.clone();
        }
        
        if(iimage.cols>new_width){
            reduce_vertical_seam_trivial(iimage, oimage);
            iimage = oimage.clone();
        }
    }
    
    out_image = oimage.clone();
    return true;
}

// horizontl trivial seam is a seam through the center of the image
bool reduce_horizontal_seam_trivial(Mat& in_image, Mat& out_image){

    // retrieve the dimensions of the new image
    int rows = in_image.rows-1;
    int cols = in_image.cols;
    
    // create an image slighly smaller
    out_image = Mat(rows, cols, CV_8UC3);
    
    //populate the image
    int middle = in_image.rows / 2;
    
    for(int i=0;i<=middle;++i)
        for(int j=0;j<cols;++j){
            Vec3b pixel = in_image.at<Vec3b>(i, j);
            
            /* at operator is r/w
            pixel[0] = 255;
            pixel[1] =255;
            pixel[2]=255;
            */
            
            
            
            out_image.at<Vec3b>(i,j) = pixel;
        }
    
    for(int i=middle+1;i<rows;++i)
        for(int j=0;j<cols;++j){
            Vec3b pixel = in_image.at<Vec3b>(i+1, j);
            
            /* at operator is r/w
             pixel[0] --> red
             pixel[1] --> green
             pixel[2] --> blue
             */
            
            
            out_image.at<Vec3b>(i,j) = pixel;
        }

    return true;
}

// vertical trivial seam is a seam through the center of the image
bool reduce_vertical_seam_trivial(Mat& in_image, Mat& out_image){
    // retrieve the dimensions of the new image
    int rows = in_image.rows;
    int cols = in_image.cols-1;
    
    // create an image slighly smaller
    out_image = Mat(rows, cols, CV_8UC3);
    
    //populate the image
    int middle = in_image.cols / 2;
    
    for(int i=0;i<rows;++i)
        for(int j=0;j<=middle;++j){
            Vec3b pixel = in_image.at<Vec3b>(i, j);
            
            /* at operator is r/w
             pixel[0] --> red
             pixel[1] --> green
             pixel[2] --> blue
             */
            
            
            out_image.at<Vec3b>(i,j) = pixel;
        }
    
    for(int i=0;i<rows;++i)
        for(int j=middle+1;j<cols;++j){
            Vec3b pixel = in_image.at<Vec3b>(i, j+1);
            
            /* at operator is r/w
             pixel[0] --> red
             pixel[1] --> green
             pixel[2] --> blue
             */
            
            
            out_image.at<Vec3b>(i,j) = pixel;
        }
    
    return true;
}





void vertical_energy(Mat& in_image) {
	int width = in_image.cols;
	int height = in_image.rows;
	for (int i = 0; i < width; ++i) {
		for (int j = 0; j < height; ++j) {
			image[i][j].energy = image[i][j].gray;
		}
	}

	for (int j = 1; j <= height - 1; ++j) {
		//first col
		if (image[0][j - 1].energy <= image[1][j - 1].energy) {
			image[0][j].energy = image[0][j].energy + image[0][j - 1].energy;
			image[0][j].path = 0;
		}
		else {
			image[0][j].energy = image[0][j].energy + image[1][j - 1].energy;
			image[0][j].path = 1;
		}

		//cols in middle
		for (int i = 1; i < width - 1; ++i) {
			if (image[i][j - 1].energy <= image[i - 1][j - 1].energy && image[i][j - 1].energy <= image[i + 1][j - 1].energy) {
				image[i][j].energy = image[i][j].energy + image[i][j - 1].energy;
				image[i][j].path = 0;
			}
			else if (image[i - 1][j - 1].energy <= image[i][j - 1].energy && image[i - 1][j - 1].energy <= image[i + 1][j - 1].energy) {
				image[i][j].energy = image[i][j].energy + image[i - 1][j - 1].energy;
				image[i][j].path = -1;
			}
			else if (image[i + 1][j - 1].energy <= image[i - 1][j - 1].energy && image[i + 1][j - 1].energy <= image[i][j - 1].energy) {
				image[i][j].energy = image[i][j].energy + image[i + 1][j - 1].energy;
				image[i][j].path = 1;
			}
		}

		//end col
		if (image[width - 1][j - 1].energy <= image[width - 2][j - 1].energy) {
			image[width - 1][j].energy = image[width - 1][j].energy + image[width - 1][j - 1].energy;
			image[width - 1][j].path = 0;
		}
		else {
			image[width - 1][j].energy = image[width - 1][j].energy + image[width - 2][j - 1].energy;
			image[width - 1][j].path = -1;
		}
	}
}


void vertical_path(Mat& in_image, Mat& out_image) {
	int width = in_image.cols;
	int height = in_image.rows;

	// retrieve the dimensions of the new image
	int newwidth = in_image.cols - 1;

	// create an image slighly smaller
	out_image = Mat(newwidth,height, CV_8UC3);

	int minenergy = 10000000;
	int tempx = 0;
	for (int i = 0; i < width; ++i) {
		if (image[i][height - 1].energy < minenergy) {
			minenergy = image[i][height - 1].energy;
			tempx = i;
		}
	}

	int k = tempx;
	for (int j = height - 1; j >= 0; --j) {
		int tempk = image[k][j].path;
		for (int i = 0; i < k; ++i) {
			Vec3b pixel = in_image.at<Vec3b>(j, i);
			out_image.at<Vec3b>(j, i) = pixel;
		}
		for (int i = k; i < width - 1; ++i) {
			Vec3b pixel = in_image.at<Vec3b>(j, i + 1);
			out_image.at<Vec3b>(j, i) = pixel;
			image[i][j].gray = image[i + 1][j].gray;
			image[i][j].energy = image[i + 1][j].energy;
			image[i][j].path= image[i + 1][j].path;
		}
		k = k + tempk;
	}
}