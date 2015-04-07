class PostsController < ApplicationController
  def index
	@users = User.all
	render json: @users
  end
  
  def create
  
  end
  
  def new
  
  end
end
