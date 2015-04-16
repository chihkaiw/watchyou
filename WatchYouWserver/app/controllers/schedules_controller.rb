class SchedulesController < ApplicationController
  def new
	@userID = params[:userID]
	@schedule = Schedule.new
  end
  def index
	@userID = params[:userID]
	@schedule = Schedule.where(:userID => params[:userID])
  end
  
  def create
	@schedule = Schedule.new(post_params)
	@schedule.userID = params[:userID]
	if @schedule.save
		redirect_to schedules_index_path(:userID => @schedule.userID), notice => "saved"
	else
		render "new"
	end
  end
  def update
  end

  def delete
	if Schedule.destroy(params[:id])
			redirect_to schedules_path, :notice => "saved"
		else
			render "Show"
		end
  end
  
  def show
	@schedule = Schedule.find(params[:id])
  end
  
  def json
	@schedule = Schedule.where(:userID => params[:userID])
    render json: @schedule
  end
  
  private
    def post_params
        params.require(:schedule).permit(:date, :title, :category, :note, :star)
    end
end
