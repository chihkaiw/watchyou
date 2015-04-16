class UsersController < ApplicationController
  def index
	@user = User.all
  end
  
  def new
	@user = User.new
  end
	
  def create
	@user = User.new(post_params)
	if @user.save
		redirect_to users_index_path, notice => "saved"
	else
		render "new"
	end
  end
	
  def update
		@user = User.find(params[:id])
		
		if @user.update_attributes(post_params)
			redirect_to user_path, :notice => "saved"
		else
			render "Edit"
		end
  end

  def delete
		@schedule = Schedule.where(:userID => params[:id])
		if @schedule.destroy_all && User.destroy(params[:id])
			redirect_to users_index_path, :notice => "saved"
		else
			render "show"
		end
  end
  
  def show
  	@user = User.find(params[:id])
  end
  
  def json
	@user = User.all
    render json: @user
  end
  private
    def post_params
        params.require(:user).permit(:name, :email, :password)
    end

end
